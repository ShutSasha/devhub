import { RouterProvider } from 'react-router-dom'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import GlobalStyle from '@app/styles/global.styles'
import { setAccessToken, setLoading, setUser } from '@features/user/user.slice'
import router from '@pages/router'
import { useEffect } from 'react'
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

import { RefreshResponse } from '~types/auth/refresh-response.type'

const baseUrl = `${process.env.REACT_APP_API_URL}`

export const App = () => {
  const user = useAppSelector(state => state.userSlice.user)
  const dispatch = useAppDispatch()

  useEffect(() => {
    const refreshUserToken = async () => {
      try {
        dispatch(setLoading(true))

        const response = await fetch(`${baseUrl}/auth/refresh`, {
          method: 'POST',
          credentials: 'include',
        })

        const { accessToken, user }: RefreshResponse = await response.json()

        dispatch(setAccessToken(accessToken))
        dispatch(setUser(user))
      } catch (e) {
        console.error(e)
      } finally {
        dispatch(setLoading(false))
      }
    }

    if (!user) refreshUserToken()
  }, [user])

  return (
    <>
      <GlobalStyle />
      <ToastContainer />
      <RouterProvider router={router} />
    </>
  )
}

import { SearchInput } from '@shared/components/search-input/search-input.component'
import { MainLayout } from '@shared/layouts/main.layout'
import { ROUTES } from '@pages/router/routes.enum'
import { useAppSelector } from '@app/store/store'
import { useNavigate } from 'react-router-dom'
import { useEffect } from 'react'

import * as _ from './followers.style'

export const Followers = () => {
  const navigate = useNavigate()
  const user = useAppSelector(state => state.userSlice.user)
  const currentUrl = window.location.href

  useEffect(() => {
    if (!user) {
      navigate(ROUTES.HOME)
    }
  }, [user, navigate])

  if (!user) {
    return null
  }

  return (
    <MainLayout>
      <div></div>
      <div>
        <SearchInput placeholder="Search by users..." />
        <_.ChangeList>
          <_.ListText
            $underline={currentUrl.includes(ROUTES.USER_FRIENDS_FOLLOWERS.replace(':id', ''))}
            to={ROUTES.USER_FRIENDS_FOLLOWERS.replace(':id', user._id)}
          >
            Followers
          </_.ListText>
          <_.ListText
            $underline={currentUrl.includes(ROUTES.USER_FRIENDS_FOLLOWED_LIST.replace(':id', ''))}
            to={ROUTES.USER_FRIENDS_FOLLOWED_LIST.replace(':id', user._id)}
          >
            Followed
          </_.ListText>
        </_.ChangeList>
      </div>
      <div></div>
    </MainLayout>
  )
}

import { RootState } from '@app/store/store'
import { logout, setAccessToken, setUser } from '@features/user/user.slice'
import { fetchBaseQuery } from '@reduxjs/toolkit/query'
import type { BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query'
import { Mutex } from 'async-mutex'

import { RefreshResponse } from '~types/auth/refresh-response.type'
// create a new mutex
const mutex = new Mutex()

const baseUrl = `${import.meta.env.VITE_API_URL}`

const baseQuery = fetchBaseQuery({
  baseUrl,
  prepareHeaders: (headers, { getState }) => {
    const token = (getState() as RootState).userSlice.accessToken

    // If we have a token set in state, let's assume that we should be passing it.
    if (token) {
      headers.set('authorization', `Bearer ${token}`)
    }

    return headers
  },
  credentials: 'include',
})

const baseQueryWithReauth: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (
  args,
  api,
  extraOptions,
) => {
  // wait until the mutex is available without locking it
  await mutex.waitForUnlock()

  let result = await baseQuery(args, api, extraOptions)

  if (result.error?.status === 401) {
    // checking whether the mutex is locked
    if (!mutex.isLocked()) {
      const release = await mutex.acquire()
      try {
        const refreshResult = (await baseQuery(
          { credentials: 'include', url: '/auth/refresh', method: 'POST' },
          api,
          extraOptions,
        )) as { data: RefreshResponse }

        api.dispatch(setAccessToken(refreshResult.data.accessToken))
        api.dispatch(setUser(refreshResult.data.user))

        if (refreshResult.data.accessToken) {
          // retry the initial query
          result = await baseQuery(args, api, extraOptions)
        } else {
          api.dispatch(logout())
        }
      } finally {
        // release must be called once the mutex should be released again.
        release()
      }
    } else {
      // wait until the mutex is available without locking it
      await mutex.waitForUnlock()
      result = await baseQuery(args, api, extraOptions)
    }
  }
  return result
}

export default baseQueryWithReauth

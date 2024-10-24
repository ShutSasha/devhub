import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { IUser } from '~types/user/user.type'
import { SignUpDto } from '~types/user/user.dto'

export const api = createApi({
  reducerPath: 'authApi',
  baseQuery: baseQueryWithReauth,
  endpoints: builder => ({
    register: builder.mutation<any, SignUpDto>({
      query: body => ({
        url: 'auth/register',
        method: 'POST',
        body,
      }),
    }),
  }),
})

export const { useRegisterMutation } = api

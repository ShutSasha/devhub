import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { IUser } from '~types/user/user.type'
import { UserDto } from '~types/user/user.dto'

export const api = createApi({
  reducerPath: 'userApi',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['Auth'],
  endpoints: builder => ({
    register: builder.mutation<IUser[], UserDto>({
      query: body => ({
        url: 'auth/register',
        method: 'POST',
        body,
      }),
    }),
  }),
})

export const { useRegisterMutation } = api

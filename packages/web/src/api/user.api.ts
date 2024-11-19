import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { IUser, ReqEditUserData, UserDetailsResponse } from '~types/user/user.type'

export const api = createApi({
  reducerPath: 'userApi',
  baseQuery: baseQueryWithReauth,
  endpoints: builder => ({
    getUserDetails: builder.query<UserDetailsResponse, { userId: string | undefined }>({
      query: ({ userId }) => ({
        url: `users/user-details/${userId}`,
        method: 'GET',
      }),
    }),
    getUserReactions: builder.query<{ likePosts: string[]; dislikePosts: string[] }, { userId: string }>({
      query: ({ userId }) => ({
        url: `users/user-reaction/${userId}`,
        method: 'GET',
      }),
    }),
    editUserData: builder.mutation<{ user: IUser }, ReqEditUserData>({
      query: body => ({
        url: `users`,
        method: 'PATCH',
        body: body,
      }),
    }),
    editUserPhoto: builder.mutation<{ avatar: string }, { id: string | undefined; body: FormData }>({
      query: ({ id, body }) => ({
        url: `users/update-photo/${id}`,
        method: 'POST',
        body,
      }),
    }),
  }),
})

export const {
  useGetUserDetailsQuery,
  useLazyGetUserReactionsQuery,
  useEditUserDataMutation,
  useEditUserPhotoMutation,
} = api

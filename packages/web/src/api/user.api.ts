import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { IUser, ReqEditUserData, UserDetailsResponse, UserFollowersResponse } from '~types/user/user.type'

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
    getUserReactions: builder.query<{ likedPosts: string[]; dislikedPosts: string[] }, { userId: string | undefined }>({
      query: ({ userId }) => ({
        url: `users/user-reactions/${userId}`,
        method: 'GET',
      }),
    }),
    getUserFollowers: builder.query<UserFollowersResponse[], { userId: string | undefined }>({
      query: ({ userId }) => ({
        url: `users/user-followers/${userId}`,
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
  useGetUserReactionsQuery,
  useEditUserDataMutation,
  useEditUserPhotoMutation,
  useGetUserFollowersQuery,
} = api

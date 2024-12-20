import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import {
  IUser,
  ReqEditUserData,
  UserDetailsResponse,
  UserFollowersResponse,
  UserFollowingsResponse,
} from '~types/user/user.type'
import { IReport } from '~types/post/post.type'

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
    getUserFollowings: builder.query<UserFollowingsResponse, { userId: string | undefined }>({
      query: ({ userId }) => ({
        url: `users/user-followings/${userId}`,
        method: 'GET',
      }),
    }),
    addFollowingUserFollowing: builder.mutation<
      any,
      { userId: string | undefined; followingUserId: string | undefined }
    >({
      query: body => ({
        url: `users/user-followings`,
        method: 'POST',
        body: body,
      }),
    }),
    deleteUserFollowing: builder.mutation<any, { userId: string | undefined; followingUserId: string | undefined }>({
      query: ({ userId, followingUserId }) => ({
        url: `users/user-followings?userId=${userId}&followingUserId=${followingUserId}`,
        method: 'DELETE',
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
    reportPost: builder.mutation<any, { sender: string | undefined; content: string | undefined; category: string }>({
      query: body => ({
        url: `reports`,
        method: 'POST',
        body,
      }),
    }),
    getReportsByUser: builder.query<IReport[], { userId: string | undefined }>({
      query: ({ userId }) => ({
        url: `reports/${userId}`,
        method: 'GET',
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
  useGetUserFollowingsQuery,
  useDeleteUserFollowingMutation,
  useAddFollowingUserFollowingMutation,
  useReportPostMutation,
  useLazyGetReportsByUserQuery,
} = api

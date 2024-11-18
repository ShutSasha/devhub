import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { IPost } from '~types/post/post.type'

export const api = createApi({
  reducerPath: 'postApi',
  baseQuery: baseQueryWithReauth,
  endpoints: builder => ({
    getPostById: builder.query<IPost, { id: string | undefined }>({
      query: ({ id }) => ({
        url: `posts/${id}`,
        method: 'GET',
      }),
    }),
    getPosts: builder.query<IPost[], { page: number; limit: number }>({
      query: ({ page, limit }) => ({
        url: 'posts',
        method: 'GET',
        params: { page, limit },
      }),
    }),
    createPost: builder.mutation<IPost, FormData>({
      query: body => ({
        url: 'posts',
        method: 'POST',
        body,
      }),
    }),
  }),
})

export const { useGetPostsQuery, useLazyGetPostsQuery, useCreatePostMutation, useGetPostByIdQuery } = api

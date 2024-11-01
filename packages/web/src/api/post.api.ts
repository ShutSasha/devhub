import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { IPost } from '~types/post/post.type'

export const api = createApi({
  reducerPath: 'postApi',
  baseQuery: baseQueryWithReauth,
  endpoints: builder => ({
    getPosts: builder.query<IPost[], void>({
      query: () => ({
        url: 'posts',
        method: 'GET',
      }),
    }),
  }),
})

export const { useGetPostsQuery } = api
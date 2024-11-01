import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { IPost } from '~types/post/post.type'
import { PostDto } from '~types/post/post.dto'

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
    createPost: builder.mutation<IPost, PostDto>({
      query: body => ({
        url: 'posts',
        method: 'POST',
        body,
      }),
    }),
  }),
})

export const { useGetPostsQuery } = api

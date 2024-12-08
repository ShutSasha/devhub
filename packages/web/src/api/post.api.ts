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
    dislike: builder.mutation<IPost, { postId: string; userId: string | undefined }>({
      query: ({ postId, userId }) => ({
        url: `posts/${postId}/dislike`,
        method: 'POST',
        body: { userId },
      }),
    }),
    like: builder.mutation<IPost, { postId: string; userId: string | undefined }>({
      query: ({ postId, userId }) => ({
        url: `posts/${postId}/like`,
        method: 'POST',
        body: { userId },
      }),
    }),
    editPost: builder.mutation<IPost, { postId: string | undefined; body: FormData }>({
      query: ({ postId, body }) => ({
        url: `posts/${postId}`,
        method: 'PATCH',
        body,
      }),
    }),
    deletePost: builder.mutation<void, { id: string | undefined }>({
      query: ({ id }) => ({
        url: `posts/${id}`,
        method: 'DELETE',
      }),
    }),
  }),
})

export const {
  useGetPostsQuery,
  useLazyGetPostsQuery,
  useCreatePostMutation,
  useGetPostByIdQuery,
  useDislikeMutation,
  useLikeMutation,
  useEditPostMutation,
  useDeletePostMutation,
} = api

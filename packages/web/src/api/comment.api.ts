import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { CommentDto, ICommentResponse } from '~types/comment/comment.type'

export const api = createApi({
  reducerPath: 'commentApi',
  baseQuery: baseQueryWithReauth,
  endpoints: builder => ({
    getComment: builder.query<ICommentResponse, { id: string }>({
      query: ({ id }) => ({
        url: `comments/${id}`,
        method: 'GET',
      }),
    }),
    deleteCommentById: builder.mutation<{ message: string }, { id: string }>({
      query: ({ id }) => ({
        url: `comments/${id}`,
        method: 'DELETE',
      }),
    }),
    createComment: builder.mutation<ICommentResponse, CommentDto>({
      query: body => ({
        url: 'comments',
        method: 'POST',
        body,
      }),
    }),
  }),
})

export const { useCreateCommentMutation, useLazyGetCommentQuery, useDeleteCommentByIdMutation } = api

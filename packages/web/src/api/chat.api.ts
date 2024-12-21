import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { CommentDto, ICommentResponse } from '~types/comment/comment.type'
import {ChatResponse} from "~types/chat/chat.type";

export const api = createApi({
    reducerPath: 'chatApi',
    baseQuery: baseQueryWithReauth,
    endpoints: builder => ({
        getChatsByUser: builder.query<ChatResponse, { userId: string | undefined }>({
            query: ({ userId }) => ({
                url: `chat/chat-details/${userId}`,
                method: 'GET',
            }),
        }),
        // deleteCommentById: builder.mutation<{ message: string }, { id: string }>({
        //     query: ({ id }) => ({
        //         url: `comments/${id}`,
        //         method: 'DELETE',
        //     }),
        // }),
        // createComment: builder.mutation<ICommentResponse, CommentDto>({
        //     query: body => ({
        //         url: 'comments',
        //         method: 'POST',
        //         body,
        //     }),
        // }),
    }),
})

export const {useGetChatsByUserQuery} = api

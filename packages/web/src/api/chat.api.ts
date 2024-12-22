import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { ChatResponse, MainChatResponse } from '~types/chat/chat.type'

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
    getFirstChat: builder.query<MainChatResponse, { userId: string | undefined }>({
      query: ({ userId }) => ({
        url: `chat/main-chat/${userId}`,
        method: 'GET',
      }),
    }),

    getChatById: builder.query<MainChatResponse, { chatId: string | undefined; userId: string | undefined }>({
      query: ({ chatId, userId }) => ({
        url: `chat/${chatId}/${userId}`,
        method: 'GET',
      }),
    }),
  }),
})

export const { useGetChatsByUserQuery, useGetFirstChatQuery, useGetChatByIdQuery } = api

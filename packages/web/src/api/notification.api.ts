import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { GetNotificationsRes } from '~types/notifications/notifications.types'

export const api = createApi({
  reducerPath: 'notificationApi',
  baseQuery: baseQueryWithReauth,
  endpoints: builder => ({
    getNotificationsByUser: builder.query<GetNotificationsRes, { user_id: string | undefined }>({
      query: ({ user_id }) => ({
        url: `notifications/${user_id}`,
        method: 'GET',
      }),
    }),
    readNotificationById: builder.mutation<{ message: string }, { notification_id: string | undefined }>({
      query: ({ notification_id }) => ({
        url: `notifications/${notification_id}`,
        method: 'PATCH',
      }),
    }),
  }),
})

export const { useGetNotificationsByUserQuery, useReadNotificationByIdMutation, useLazyGetNotificationsByUserQuery } =
  api

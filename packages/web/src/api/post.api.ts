import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

export const api = createApi({
  reducerPath: 'postApi',
  baseQuery: baseQueryWithReauth,
  endpoints: builder => ({
    getPosts: builder.query<any, any>({
      query: () => ({
        // TODO: change to correct endpoint
        url: 'auth/new-endpoint',
        method: 'GET',
      }),
    }),
  }),
})

export const { useGetPostsQuery } = api

import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

import { User } from '~types/user.type'

export const api = createApi({
  reducerPath: 'userApi',
  baseQuery: fetchBaseQuery({
    baseUrl: 'https://jsonplaceholder.typicode.com',
  }),
  tagTypes: ['User'],
  endpoints: builder => ({
    getUsers: builder.query<User[], void>({
      query: () => 'users',
    }),
  }),
})

export const { useGetUsersQuery } = api

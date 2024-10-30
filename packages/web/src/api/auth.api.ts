import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { SignUpDto } from '~types/auth/sign-up.dto'
import { IUser } from '~types/user/user.type'
import { VerifyEmailDto, VerifyEmailResponse } from '~types/auth/verify-email.type'
import { LoginResponse } from '~types/auth/login-response.type'
import { LoginDto } from '~types/auth/login.dto'

export const api = createApi({
  reducerPath: 'authApi',
  baseQuery: baseQueryWithReauth,
  endpoints: builder => ({
    register: builder.mutation<IUser, SignUpDto>({
      query: body => ({
        url: 'auth/register',
        method: 'POST',
        body,
      }),
    }),
    verifyEmail: builder.mutation<VerifyEmailResponse, VerifyEmailDto>({
      query: body => ({
        url: 'auth/verify-email',
        method: 'POST',
        body,
      }),
    }),
    login: builder.mutation<LoginResponse, LoginDto>({
      query: body => ({
        url: 'auth/login',
        method: 'POST',
        body,
      }),
    }),
  }),
})

export const { useRegisterMutation, useVerifyEmailMutation, useLoginMutation } = api

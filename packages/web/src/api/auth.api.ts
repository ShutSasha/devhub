import { createApi } from '@reduxjs/toolkit/query/react'

import baseQueryWithReauth from './baseQueryWithReauth'

import { SignUpDto } from '~types/auth/sign-up.dto'
import { IUser } from '~types/user/user.type'
import { VerifyEmailDto, VerifyEmailResponse } from '~types/auth/verify-email.type'
import { LoginResponse } from '~types/auth/login-response.type'
import { LoginDto } from '~types/auth/login.dto'
import { ForgetPasswordRequest } from '~types/auth/forget-password-verify.type'
import { ChangePasswordRequest } from '~types/auth/change-password.type'

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
    logout: builder.mutation<void, void>({
      query: () => ({
        url: 'auth/logout',
        method: 'POST',
      }),
    }),
    passwordVerificationCode: builder.mutation<{ message: string }, ForgetPasswordRequest>({
      query: body => ({
        url: 'auth/password-verification-code',
        method: 'PATCH',
        body,
      }),
    }),
    changePassword: builder.mutation<{ message: string }, ChangePasswordRequest>({
      query: body => ({
        url: 'auth/change-password',
        method: 'PATCH',
        body,
      }),
    }),
    googleAuth: builder.query<void, void>({
      query: () => ({
        url: 'auth/google-login',
        method: 'GET',
      }),
    }),
    githubAuth: builder.query<void, void>({
      query: () => ({
        url: 'auth/github-login',
        method: 'GET',
      }),
    }),
  }),
})

export const {
  useRegisterMutation,
  useVerifyEmailMutation,
  useLoginMutation,
  usePasswordVerificationCodeMutation,
  useChangePasswordMutation,
  useLogoutMutation,
  useLazyGoogleAuthQuery,
  useLazyGithubAuthQuery,
} = api

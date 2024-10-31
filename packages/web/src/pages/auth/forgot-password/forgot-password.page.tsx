import { ChangeEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { AuthDevhubTitle } from '@shared/components/auth/devhub-title/auth-devhub-title.components'
import { AuthTitle } from '@shared/components/auth/title/auth-title.component'
import { AuthLayout } from '@shared/layouts/auth/auth.layout'
import { handleServerException } from '@utils/handleServerException.util'
import loaderGif from '@assets/gif/loading.gif'
import { AuthInput } from '@shared/components/auth/input/auth-input.component'
import { ErrorSpan } from '@shared/components/errors/error-span.component'
import { AuthBtn } from '@shared/components/auth/btn/btn.component'
import { usePasswordVerificationCodeMutation } from '@api/auth.api'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { setForgetPasswordEmail } from '@features/auth/auth.slice'
import { ROUTES } from '@pages/router/routes.enum'

import { ErrorException } from '~types/error/error.type'

export const ForgotPassword = () => {
  const navigate = useNavigate()
  const [passwordVetificationCode, { isLoading, error }] = usePasswordVerificationCodeMutation()

  const email = useAppSelector(state => state.authSlice.forgetPasswordEmail)
  const dispatch = useAppDispatch()

  const handleSendCode = async (): Promise<void> => {
    try {
      await passwordVetificationCode({ email }).unwrap()

      navigate(ROUTES.FORGOT_PASSWORD_VERIFY)
    } catch (e) {
      console.error(e)
    }
  }

  const handleChangeInput = (e: ChangeEvent<HTMLInputElement>): void => {
    dispatch(setForgetPasswordEmail(e.target.value))
  }

  const errors: string[] | undefined = handleServerException(error as ErrorException)

  return (
    <AuthLayout>
      <AuthDevhubTitle />
      <AuthTitle title="Forgot password" style={{ marginBottom: '16px', textAlign: 'center' }} />
      {isLoading ? (
        <img src={loaderGif} />
      ) : (
        <AuthInput label="Email" value={email} handleInput={handleChangeInput} style={{ marginBottom: '16px' }} />
      )}
      <div>{errors && errors.map((error: string, index: number) => <ErrorSpan key={index} value={error} />)}</div>
      <AuthBtn handleClick={handleSendCode} text="Send code" />
    </AuthLayout>
  )
}

import { ChangeEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useChangePasswordMutation } from '@api/auth.api'
import { useAppSelector } from '@app/store/store'
import { AuthBtn } from '@shared/components/auth/btn/btn.component'
import { AuthDevhubTitle } from '@shared/components/auth/devhub-title/auth-devhub-title.components'
import { AuthInput } from '@shared/components/auth/input/auth-input.component'
import { AuthTitle } from '@shared/components/auth/title/auth-title.component'
import { ErrorSpan } from '@shared/components/errors/error-span.component'
import { AuthLayout } from '@shared/layouts/auth/auth.layout'
import { handleServerException } from '@utils/handleServerException.util'
import loaderGif from '@assets/gif/loading.gif'
import { ROUTES } from '@pages/router/routes.enum'

import { ErrorException } from '~types/error/error.type'

export const EnterNewPassword = () => {
  const navigate = useNavigate()

  const email = useAppSelector(state => state.authSlice.forgetPasswordEmail)
  const [changePassword, { isLoading, error }] = useChangePasswordMutation()
  const [password, setPassword] = useState<string>('')
  const [repeatPassword, setRepeatPassword] = useState<string>('')

  const handleConfirm = async (): Promise<void> => {
    try {
      await changePassword({ email, password, repeatPassword }).unwrap()

      navigate(ROUTES.HOME)
    } catch (e) {
      console.error(e)
    }
  }

  const handleChangePassword = (e: ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value)
  }

  const handleChangeRepeatPassword = (e: ChangeEvent<HTMLInputElement>) => {
    setRepeatPassword(e.target.value)
  }

  const errors: string[] | undefined = handleServerException(error as ErrorException)

  return (
    <AuthLayout>
      <AuthDevhubTitle />
      <AuthTitle title="Forgot password" style={{ marginBottom: '16px', textAlign: 'center' }} />
      {isLoading ? (
        <img src={loaderGif} />
      ) : (
        <>
          <AuthInput
            label="Set new password"
            value={password}
            handleInput={handleChangePassword}
            style={{ marginBottom: '16px' }}
            type="password"
          />
          <AuthInput
            label="Repeat new password"
            value={repeatPassword}
            handleInput={handleChangeRepeatPassword}
            style={{ marginBottom: '16px' }}
            type="password"
          />
        </>
      )}
      <div>{errors && errors.map((error: string, index: number) => <ErrorSpan key={index} value={error} />)}</div>
      <AuthBtn handleClick={handleConfirm} text="Confirm" />
    </AuthLayout>
  )
}

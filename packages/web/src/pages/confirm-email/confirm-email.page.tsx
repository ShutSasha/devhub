import { ChangeEvent, useState } from 'react'
import { useVerifyEmailMutation } from '@api/auth.api'
import { useAppSelector } from '@app/store/store'
import { AuthBtn } from '@shared/components/auth/btn/btn.component'
import { AuthDevhubTitle } from '@shared/components/auth/devhub-title/auth-devhub-title.components'
import { AuthInput } from '@shared/components/auth/input/auth-input.component'
import { AuthTitle } from '@shared/components/auth/title/auth-title.component'
import { ErrorSpan } from '@shared/components/errors/error-span.component'
import { AuthLayout } from '@shared/layouts/auth/auth.layout'
import { handleServerException } from '@utils/handleServerException.util'

import { ErrorException } from '~types/error/error.type'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '@pages/router/routes.enum'

export const ConfirmEmail = () => {
  const navigate = useNavigate()

  const email = useAppSelector(state => state.authSlice.email)
  const [verifyEmail, { isLoading, error }] = useVerifyEmailMutation()
  const [code, setCode] = useState<string>('')

  const handleConfirm = async (): Promise<void> => {
    try {
      const res = await verifyEmail({ email, activationCode: code }).unwrap()
      console.log('response', res)

      navigate(ROUTES.HOME)
    } catch (e) {
      console.error(e)
    }
  }

  const handleChangeInput = (e: ChangeEvent<HTMLInputElement>): void => {
    setCode(e.target.value)
  }

  // const errors: string[] | undefined = handleServerException(error as ErrorException)

  return (
    <AuthLayout>
      <AuthDevhubTitle />
      <AuthTitle title="Code sent to the email" style={{ marginBottom: '16px', textAlign: 'center' }} />
      <AuthInput
        placeholder="6-digital code"
        value={code}
        handleInput={handleChangeInput}
        style={{ marginBottom: '16px' }}
      />
      {/* <div>{errors && errors.map((error: string, index: number) => <ErrorSpan key={index} value={error} />)}</div> */}
      <AuthBtn handleClick={handleConfirm} text="Confirm" />
    </AuthLayout>
  )
}

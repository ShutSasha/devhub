import { ChangeEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { AuthDevhubTitle } from '@shared/components/auth/devhub-title/auth-devhub-title.components'
import { AuthTitle } from '@shared/components/auth/title/auth-title.component'
import { AuthLayout } from '@shared/layouts/auth/auth.layout'
import { handleServerException } from '@utils/handleServerException.util'
import loaderGif from '@assets/gif/loading.gif'
import { AuthInput } from '@shared/components/auth/input/auth-input.component'
import { ErrorSpan } from '@shared/components/errors/error-span.component'
import { AuthBtn } from '@shared/components/auth/btn/btn.component'

import { ErrorException } from '~types/error/error.type'

export const ForgotPassword = () => {
  const navigate = useNavigate()

  const [email, setEmail] = useState<string>('')

  const handleSendCode = async (): Promise<void> => {
    try {
      // api method here
      // navigate(ROUTES.HOME) - change route to page with confirm
    } catch (e) {
      console.error(e)
    }
  }

  const handleChangeInput = (e: ChangeEvent<HTMLInputElement>): void => {
    setEmail(e.target.value)
  }

  // TODO handle error when api has been added
  // const errors: string[] | undefined = handleServerException(error as ErrorException)

  return (
    <AuthLayout>
      <AuthDevhubTitle />
      <AuthTitle title="Forgot password" style={{ marginBottom: '16px', textAlign: 'center' }} />
      {/* change to isLoading state, when api has been added */}
      {/* {isLoading ? (
        <img src={loaderGif} />
      ) : (
        <AuthInput label="Email" value={email} handleInput={handleChangeInput} style={{ marginBottom: '16px' }} />
      )} */}
      <AuthInput label="Email" value={email} handleInput={handleChangeInput} style={{ marginBottom: '16px' }} />
      {/* added errro visible when api has been added */}
      {/* <div>{errors && errors.map((error: string, index: number) => <ErrorSpan key={index} value={error} />)}</div> */}
      <AuthBtn handleClick={handleSendCode} text="Send code" />
    </AuthLayout>
  )
}

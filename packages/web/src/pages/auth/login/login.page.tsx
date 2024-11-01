import { useNavigate } from 'react-router-dom'
import { AuthDevhubTitle } from '@shared/components/auth/devhub-title/auth-devhub-title.components'
import { AuthInput } from '@shared/components/auth/input/auth-input.component'
import { AuthTitle } from '@shared/components/auth/title/auth-title.component'
import { AuthLayout } from '@shared/layouts/auth/auth.layout'
import { ChangeEvent, useState } from 'react'
import { AuthBtn } from '@shared/components/auth/btn/btn.component'
import { EmphasizeLine } from '@shared/components/auth/emphasize-line/emphasize-line.component'
import googleImage from '@assets/images/auth/devicon_google.svg'
import githubImage from '@assets/images/auth/mdi_github.svg'
import { Text } from '@shared/components/text/text.component'
import { FONTS } from '@shared/consts/fonts.enum'
import { AuthIcon, ImgContainer } from '@pages/auth/sign-up/sign-up.style'
import { useLoginMutation } from '@api/auth.api'
import { useAppDispatch } from '@app/store/store'
import { setAccessToken, setUser } from '@features/user/user.slice'
import { ROUTES } from '@pages/router/routes.enum'
import { handleServerException } from '@utils/handleServerException.util'
import { ErrorSpan } from '@shared/components/errors/error-span.component'

import { ForgotPassword } from './components/forgot-password.component'
import { CreateAccount } from './components/create-account.component'

import { ErrorException } from '~types/error/error.type'

export const Login = () => {
  const [login, { error: loginError }] = useLoginMutation()
  const dispatch = useAppDispatch()
  const navigate = useNavigate()

  const [username, setUsername] = useState<string>('')
  const [password, setPassword] = useState<string>('')

  const handleUsernameInput = (e: ChangeEvent<HTMLInputElement>) => {
    setUsername(e.target.value)
  }

  const handlePasswordInput = (e: ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value)
  }

  const handleLogin = async () => {
    try {
      const { accessToken, user } = await login({ username, password }).unwrap()
      dispatch(setAccessToken(accessToken))
      dispatch(setUser(user))

      navigate(ROUTES.HOME)
    } catch (e) {
      console.error(e)
    }
  }

  const errors: string[] | undefined = handleServerException(loginError as ErrorException)

  return (
    <AuthLayout>
      <AuthDevhubTitle />
      <AuthTitle title="Welcome back!" style={{ marginBottom: '16px', textAlign: 'center' }} />
      <AuthInput label="Username" value={username} handleInput={handleUsernameInput} style={{ marginBottom: '16px' }} />
      <AuthInput
        label="Password"
        value={password}
        handleInput={handlePasswordInput}
        style={{ marginBottom: '16px' }}
        type="password"
      />
      <div style={{ width: '100%' }}>
        {errors && errors.map((error: string, index: number) => <ErrorSpan key={index} value={error} />)}
      </div>
      <ForgotPassword />
      <AuthBtn text="Sign in" handleClick={handleLogin} />
      <CreateAccount />
      <EmphasizeLine style={{ width: '100%' }} />
      <Text
        text="Sign in with"
        fontFamily={FONTS.INTER}
        fontSize="16px"
        $lineHeight="24px"
        fontWeight="400"
        style={{ marginBottom: '16px' }}
      />
      <ImgContainer>
        <AuthIcon src={googleImage} alt="GoogleAuth" />
        <AuthIcon src={githubImage} alt="GithubAuth" />
      </ImgContainer>
    </AuthLayout>
  )
}

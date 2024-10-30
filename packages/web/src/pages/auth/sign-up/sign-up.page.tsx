import { FC } from 'react'
import { useNavigate } from 'react-router-dom'
import { Link } from 'react-router-dom'
import { AuthLayout } from '@shared/layouts/auth/auth.layout'
import { Text } from '@shared/components/text/text.component'
import { FONTS } from '@shared/consts/fonts.enum'
import { AuthInput } from '@shared/components/auth/input/auth-input.component'
import { AuthTitle } from '@shared/components/auth/title/auth-title.component'
import { AuthBtn } from '@shared/components/auth/btn/btn.component'
import { AuthDevhubTitle } from '@shared/components/auth/devhub-title/auth-devhub-title.components'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { setEmail, setPassword, setRepeatPassword, setUsername } from '@features/auth/auth.slice'
import googleImage from '@assets/images/auth/devicon_google.svg'
import githubImage from '@assets/images/auth/mdi_github.svg'
import { useRegisterMutation } from '@api/auth.api'
import { handleServerException } from '@utils/handleServerException.util'
import { ErrorSpan } from '@shared/components/errors/error-span.component'
import loaderGif from '@assets/gif/loading.gif'
import { ROUTES } from '@pages/router/routes.enum'
import { AuthTransparentBtn } from '@shared/components/auth/transparent-btn/sign-up-transparent-btn'
import { EmphasizeLine } from '@shared/components/auth/emphasize-line/emphasize-line.component'

import { AuthIcon, ImgContainer, InputsContainer, SixDigitalCodeSpan } from './sign-up.style'

import { ErrorException } from '~types/error/error.type'

export const SignUp: FC = () => {
  const [register, { isLoading, error: signUpError }] = useRegisterMutation()
  const dispatch = useAppDispatch()
  const navigate = useNavigate()

  const username = useAppSelector(state => state.authSlice.username)
  const password = useAppSelector(state => state.authSlice.password)
  const repeatPassword = useAppSelector(state => state.authSlice.repeatPassword)
  const email = useAppSelector(state => state.authSlice.email)

  const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(setUsername(e.target.value))
  }

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(setPassword(e.target.value))
  }

  const handleRepeatPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(setRepeatPassword(e.target.value))
  }

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(setEmail(e.target.value))
  }

  const handleSumbit = async () => {
    try {
      await register({ username, password, repeatPassword, email }).unwrap()

      navigate(`${ROUTES.SIGN_UP}${ROUTES.CONFIRM_EMAIL}`)
    } catch (e) {
      console.error(e)
    }
  }

  const errors: string[] | undefined = handleServerException(signUpError as ErrorException)

  return (
    <AuthLayout>
      <AuthDevhubTitle />
      <AuthTitle title="Welcome!" style={{ marginBottom: '16px' }} />
      <AuthTitle title="Create account" style={{ marginBottom: '16px', textAlign: 'center' }} />
      <div style={{ width: '100%' }}>
        <InputsContainer>
          {isLoading ? (
            <img src={loaderGif} />
          ) : (
            <>
              <AuthInput label="Username" value={username || ''} handleInput={handleUsernameChange} />
              <AuthInput label="Password" type="password" value={password || ''} handleInput={handlePasswordChange} />
              <AuthInput
                label="Repeat password"
                type="password"
                value={repeatPassword || ''}
                handleInput={handleRepeatPasswordChange}
              />
              <AuthInput label="Email" type="email" value={email || ''} handleInput={handleEmailChange} />
            </>
          )}
        </InputsContainer>
        <SixDigitalCodeSpan>6-digital code will be send to email</SixDigitalCodeSpan>
        <div>{errors && errors.map((error: string, index: number) => <ErrorSpan key={index} value={error} />)}</div>
        <AuthBtn text="Sign up" handleClick={handleSumbit} />
        <div
          style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginBottom: '16px' }}
        >
          <Text text="Already have an account?" color="#0D1A26" fontSize="16px" $lineHeight="24px" />
          <Link to={ROUTES.LOGIN}>
            <AuthTransparentBtn text="Sign In" />
          </Link>
        </div>
        <EmphasizeLine />
        <Text
          text="Sign up with"
          $lineHeight="24px"
          fontSize="16px"
          fontFamily={FONTS.INTER}
          style={{ textAlign: 'center', marginBottom: '16px' }}
        />
        <ImgContainer>
          <AuthIcon src={googleImage} alt="GoogleAuth" />
          <AuthIcon src={githubImage} alt="GithubAuth" />
        </ImgContainer>
      </div>
    </AuthLayout>
  )
}

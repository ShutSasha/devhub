import { FC } from 'react'
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

import { AuthIcon, ImgContainer, InputsContainer, SixDigitalCodeSpan } from './sign-up.style'
import { SignUpTransparentBtn } from './components/sign-up-transparent-btn'

export const SignUp: FC = () => {
  const [register, { isLoading, isSuccess, isError, error }] = useRegisterMutation()
  const dispatch = useAppDispatch()

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
      const res = await register({ userName: username, password, email })
      console.log({ username, password, email })
    } catch (error) {
      console.error(error)
    }
  }

  return (
    <AuthLayout>
      <AuthDevhubTitle />
      <AuthTitle title="Welcome!" style={{ marginBottom: '16px' }} />
      <AuthTitle title="Create account" style={{ marginBottom: '16px', textAlign: 'center' }} />
      <div style={{ width: '100%' }}>
        <InputsContainer>
          <AuthInput label="Username" handleInput={handleUsernameChange} />
          <AuthInput label="Password" type="password" handleInput={handlePasswordChange} />
          <AuthInput label="Repeat password" type="password" handleInput={handleRepeatPasswordChange} />
          <AuthInput label="Email" type="email" handleInput={handleEmailChange} />
        </InputsContainer>
        <SixDigitalCodeSpan>6-digital code will be send to email</SixDigitalCodeSpan>
        <AuthBtn text="Sign up" handleClick={handleSumbit} />
        <div
          style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginBottom: '16px' }}
        >
          <Text text="Already have an account?" color="#0D1A26" fontSize="16px" $lineHeight="24px" />
          <SignUpTransparentBtn />
        </div>
        <hr
          style={{
            color: 'red',
            backgroundColor: 'rgba(48, 64, 80, 0.15)',
            height: '1px',
            border: 'none',
            marginBottom: '16px',
          }}
        />
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

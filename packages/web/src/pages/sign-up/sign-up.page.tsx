import { FC } from 'react'
import { AuthLayout } from '@shared/layouts/auth/auth.layout'
import { Text } from '@shared/components/text/text.component'
import { FONTS } from '@shared/consts/fonts.enum'
import { AuthInput } from '@shared/components/auth/input/auth-input.component'
import { AuthTitle } from '@shared/components/auth/title/auth-title.component'
import { AuthBtn } from '@shared/components/auth/btn/btn.component'
import googleImage from '@assets/images/auth/devicon_google.svg'
import githubImage from '@assets/images/auth/mdi_github.svg'
import { AuthDevhubTitle } from '@shared/components/auth/devhub-title/auth-devhub-title.components'

import { AuthIcon, ImgContainer, InputsContainer, SixDigitalCodeSpan } from './sign-up.style'
import { SignUpTransparentBtn } from './components/sign-up-transparent-btn'

export const SignUp: FC = () => {
  return (
    <AuthLayout>
      <AuthDevhubTitle />
      <AuthTitle title="Welcome!" style={{ marginBottom: '16px' }} />
      <AuthTitle title="Create account" style={{ marginBottom: '16px', textAlign: 'center' }} />
      <div style={{ width: '100%' }}>
        <InputsContainer>
          <AuthInput label="Username" />
          <AuthInput label="Password" type="password" />
          <AuthInput label="Repeat password" type="password" />
          <AuthInput label="Email" type="email" />
        </InputsContainer>
        <SixDigitalCodeSpan>6-digital code will be send to email</SixDigitalCodeSpan>
        <AuthBtn text="Sign up" />
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

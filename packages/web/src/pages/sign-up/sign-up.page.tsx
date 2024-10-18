import { FC } from 'react'
import { AuthInput } from '@shared/components/auth-input/auth-input.component'
import { AuthLayout } from '@shared/layouts/auth/auth.layout'
import { Text } from '@shared/components/text/text.component'
import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'
import { Button } from '@shared/components/button/button.component'

import { BlackText, InputsContainer, OrangeText, SixDigitalCodeSpan, StyledText } from './sign-up.style'

export const SignUp: FC = () => {
  return (
    <AuthLayout>
      <StyledText>
        <BlackText>Dev</BlackText>
        <OrangeText>Hub</OrangeText>
      </StyledText>
      <Text
        text="Welcome!"
        color={colors.text}
        fontSize="32px"
        $lineHeight="48px"
        fontWeight="700"
        fontFamily={FONTS.INTER}
      />
      <Text
        style={{ marginBottom: '16px', textAlign: 'center' }}
        text="Create account"
        color={colors.text}
        fontSize="32px"
        $lineHeight="48px"
        fontWeight="700"
        fontFamily={FONTS.INTER}
      />
      <div style={{ width: '100%' }}>
        <InputsContainer>
          <AuthInput label="Username" />
          <AuthInput label="Password" />
          <AuthInput label="Repeat password" />
          <AuthInput label="Email" />
        </InputsContainer>
        <SixDigitalCodeSpan>6-digital code will be send to email</SixDigitalCodeSpan>
        <Button
          text="Sign up"
          fontFamily={FONTS.INTER}
          fontWeight="700"
          fontSize="16px"
          bgColor={colors.accent}
          color="#fff"
          padding="5px 0"
          width="100%"
          hoverBgColor="#fff"
          hoverColor={colors.accent}
          hoverBorder={`1px solid ${colors.accent}`}
          margin="0 0 16px 0"
        />
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
          <Text text="Already have an account?" color="#0D1A26" fontSize="16px" $lineHeight="24px" />
          <Button
            text="Sign in"
            padding="3px 10px"
            bgColor="#fff"
            color={colors.accent}
            border={`1px solid ${colors.accent}`}
            fontFamily={`${FONTS.INTER}`}
            fontWeight="600"
            width="none"
            hoverBgColor={colors.accent}
            hoverColor="#fff"
            hoverBorder="1px solid transparent"
          />
        </div>
      </div>
    </AuthLayout>
  )
}

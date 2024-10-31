import { AuthTransparentBtn } from '@shared/components/auth/transparent-btn/sign-up-transparent-btn'
import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'

const StyledContainer = styled.div`
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
`

const StyledText = styled.p`
  font-family: ${FONTS.INTER};
  font-size: 16px;
  line-height: 24px;
  color: #0d1a26;
`

export const CreateAccount = () => {
  return (
    <StyledContainer>
      <StyledText>Don't have an account?</StyledText>
      <AuthTransparentBtn text="Sign Up" />
    </StyledContainer>
  )
}
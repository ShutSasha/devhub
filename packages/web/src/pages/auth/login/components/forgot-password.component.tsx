import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'
import { Link } from 'react-router-dom'
import styled from 'styled-components'

const StyledContainer = styled.div`
  width: 100%;
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
`

export const ForgotPassword = () => {
  return (
    <StyledContainer>
      <Link
        style={{ fontFamily: FONTS.INTER, fontSize: '16px', lineHeight: '24px', color: colors.text }}
        to={`/forgot-password`}
      >
        Forgot password?
      </Link>
    </StyledContainer>
  )
}

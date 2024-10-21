import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'

export const StyledText = styled.h1`
  font-size: 32px;
  line-height: 48px;
  font-weight: 600;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  display: flex;
  margin-bottom: 16px;
`

export const BlackText = styled.span`
  font-family: ${FONTS.MONTSERRAT};
  color: #000;
`

export const OrangeText = styled.span`
  font-family: ${FONTS.MONTSERRAT};
  color: #f7971d;
`

export const AuthDevhubTitle = () => {
  return (
    <StyledText>
      <BlackText>Dev</BlackText>
      <OrangeText>Hub</OrangeText>
    </StyledText>
  )
}

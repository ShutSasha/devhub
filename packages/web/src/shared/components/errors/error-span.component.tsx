import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'

const StyledErrorSpan = styled.span`
  font-family: ${FONTS.MANROPE};
  font-size: 14px;
  font-weight: 500;
  line-height: 20px;
  display: block;
  color: ${colors.wrong};
  margin-bottom: 8px;
`

export const ErrorSpan = ({ value }: { value: string }) => {
  return <StyledErrorSpan>{value}</StyledErrorSpan>
}

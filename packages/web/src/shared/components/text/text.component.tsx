import styled from 'styled-components'
import { FONTS } from '@shared/consts/fonts.enum'
import { FC } from 'react'

const StyledText = styled.p<Omit<TextProps, 'text'>>`
  font-family: ${({ fontFamily }) => fontFamily || FONTS.MANROPE};
  font-weight: ${({ fontWeight }) => fontWeight || '400'};
  font-size: ${({ fontSize }) => fontSize || '16px'};
  line-height: ${({ $lineHeight }) => $lineHeight || '1.5'};
  color: ${({ color }) => color || '#000'};
`

interface TextProps {
  text: string
  fontWeight?: string
  fontSize?: string
  $lineHeight?: string
  color?: string
  fontFamily?: string
}

export const Text: FC<TextProps> = ({ text, ...styles }) => {
  return <StyledText {...styles}>{text}</StyledText>
}

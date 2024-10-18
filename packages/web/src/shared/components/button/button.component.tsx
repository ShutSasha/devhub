import { FONTS } from '@shared/consts/fonts.enum'
import { FC, ReactNode } from 'react'
import styled from 'styled-components'

const StyledButton = styled.button<StyledButtonProps>`
  box-sizing: border-box;
  border: ${({ border }) => border || '1px solid transparent'};
  width: ${({ width }) => width || '100%'};
  padding: ${({ padding }) => padding || '0'};
  margin: ${({ margin }) => margin || '0'};
  color: ${({ color }) => color || '#fff'};
  background-color: ${({ bgColor }) => bgColor || '#007bff'};
  border-radius: 5px;
  font-family: ${({ fontFamily }) => fontFamily || FONTS.INTER};
  font-weight: ${({ fontWeight }) => fontWeight || '400'};
  font-size: ${({ fontSize }) => fontSize || '16px'};
  cursor: pointer;
  transition:
    background-color 0.3s ease,
    border-color 0.3s ease;

  &:hover {
    background-color: ${({ hoverBgColor }) => hoverBgColor || ''};
    border-color: ${({ hoverBorder }) => (hoverBorder ? hoverBorder.split(' ')[2] : '')};
    color: ${({ hoverColor }) => hoverColor || ''};
  }
`

interface StyledButtonProps {
  width?: string
  padding?: string
  margin?: string
  color?: string
  bgColor?: string
  hoverBgColor?: string
  fontFamily?: string
  fontWeight?: string
  fontSize?: string
  border?: string
  hoverBorder?: string
  hoverColor?: string
}

interface ButtonProps extends StyledButtonProps {
  text: ReactNode
}

export const Button: FC<ButtonProps> = ({ text, ...styles }) => {
  return <StyledButton {...styles}>{text}</StyledButton>
}

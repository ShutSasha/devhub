import { Button } from '@shared/components/button/button.component'
import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'
import { FC } from 'react'

interface AuthBtnProps {
  text: string
  handleClick?: () => void
}

export const AuthBtn: FC<AuthBtnProps> = ({ text, handleClick }) => {
  return (
    <Button
      text={text}
      $fontFamily={FONTS.INTER}
      $fontWeight="700"
      $fontSize="16px"
      $bgColor={colors.accent}
      $color="#fff"
      $padding="5px 0"
      $width="100%"
      $hoverBgColor="#fff"
      $hoverColor={colors.accent}
      $hoverBorder={`1px solid ${colors.accent}`}
      $margin="0 0 16px 0"
      handleClick={handleClick}
    />
  )
}

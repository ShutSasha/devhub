import { Button } from '@shared/components/button/button.component'
import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'

export const SignUpTransparentBtn = () => {
  return (
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
  )
}

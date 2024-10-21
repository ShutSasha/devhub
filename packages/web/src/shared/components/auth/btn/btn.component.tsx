import { Button } from '@shared/components/button/button.component'
import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'

export const AuthBtn = () => {
  return (
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
  )
}

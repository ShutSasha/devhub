import { CSSProperties, FC } from 'react'
import { Text } from '@shared/components/text/text.component'
import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'

interface AuthTitleProps {
  title: string
  style?: CSSProperties
}

export const AuthTitle: FC<AuthTitleProps> = ({ title, style }) => {
  return (
    <Text
      text={title}
      color={colors.text}
      fontSize="32px"
      $lineHeight="48px"
      fontWeight="700"
      fontFamily={FONTS.INTER}
      style={{ ...style }}
    />
  )
}

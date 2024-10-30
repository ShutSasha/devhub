import { FC } from 'react'
import { Link } from 'react-router-dom'
import styled from 'styled-components'
import { FONTS } from '@shared/consts/fonts.enum'

import { Text } from '../text/text.component'

const NavIcon = styled.img`
  width: 20px;
  height: 20px;
`

interface NavItemProps {
  icon: string
  navTitle: string
}

export const NavItem: FC<NavItemProps> = ({ icon, navTitle }) => {
  return (
    <Link style={{ display: 'flex', alignItems: 'center', gap: '10px' }} to={'/'}>
      <NavIcon src={icon} />
      <Text
        fontFamily={FONTS.MONTSERRAT}
        fontWeight="500"
        color="#fff"
        text={navTitle}
        fontSize="18px"
        $lineHeight="24px"
      />
    </Link>
  )
}

import { FC } from 'react'
import { Link } from 'react-router-dom'
import styled from 'styled-components'
import { FONTS } from '@shared/consts/fonts.enum'

import { Text } from '../text/text.component'

const NavIcon = styled.img`
  width: 20px;
  height: 20px;
`

const LinkContainer = styled(Link)`
  text-decoration: none;
  outline: none;
  position: relative;
  padding: 6px 10px;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 10px;
    border: 1px solid transparent;
    transition: all 0.34s ease-in;
  }

  &:hover::before {
    border-color: #fff;
  }
`

interface NavItemProps {
  icon: string
  navTitle: string
}

export const NavItem: FC<NavItemProps> = ({ icon, navTitle }) => {
  return (
    <LinkContainer style={{ display: 'flex', alignItems: 'center', gap: '10px' }} to={'/'}>
      <NavIcon src={icon} />
      <Text
        fontFamily={FONTS.MONTSERRAT}
        fontWeight="500"
        color="#fff"
        text={navTitle}
        fontSize="18px"
        $lineHeight="24px"
      />
    </LinkContainer>
  )
}

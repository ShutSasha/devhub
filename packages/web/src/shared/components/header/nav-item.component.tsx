import { FC } from 'react'
import { useNavigate } from 'react-router-dom'
import styled from 'styled-components'
import { FONTS } from '@shared/consts/fonts.enum'
import { toast } from 'react-toastify'

import { Text } from '../text/text.component'

import { IUser } from '~types/user/user.type'

const NavIcon = styled.img`
  width: 20px;
  height: 20px;
`

const LinkContainer = styled.div`
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
  path: string
  user: IUser | null
}

export const NavItem: FC<NavItemProps> = ({ icon, navTitle, path, user }) => {
  const navigate = useNavigate()

  const handleClick = () => {
    if (path.includes(':id')) {
      if (user) {
        navigate(path.replace(':id', user._id))
      } else {
        toast.error('User is not authenticated', { autoClose: 1500 })
      }
    } else {
      navigate(path)
    }
  }

  return (
    <LinkContainer
      style={{ display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer' }}
      onClick={handleClick}
    >
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

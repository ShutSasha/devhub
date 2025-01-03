import { Link } from 'react-router-dom'
import styled from 'styled-components'
import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'
import logoutIcon from '@assets/images/header/logout.svg'

export const Wrapper = styled.div`
  background-color: ${colors.background};
  margin-bottom: 20px;
`

export const Container = styled.div`
  max-width: 1440px;
  width: 100%;
  margin: 0 auto;

  display: grid;
  grid-template-columns: 300px minmax(300px, 1fr) 340px;
  align-items: center;
`

export const Logo = styled.img`
  cursor: pointer;
`

export const NavList = styled.nav`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 30px;

  padding: 12px 0;
`

export const AuthContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 22px;

  justify-self: end;
`

export const LogInLink = styled(Link)`
  color: ${colors.textPrimary};
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 500;
  font-size: 18px;
  line-height: 24px;
  padding: 7px 10px;

  text-decoration: none;
  outline: none;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 10px;
    border: 1px solid transparent;
    transition: all 0.25s ease-in;
  }

  &:hover::before {
    border-color: #fff;
  }
`

export const CreateAccountLink = styled(Link)`
  color: ${colors.textPrimary};
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 500;
  font-size: 18px;
  line-height: 24px;
  padding: 7px 12px;

  border-radius: 10px;
  border: 1px solid ${colors.textPrimary};
  box-sizing: border-box;
  transition: all 0.25s ease-in;

  &:hover {
    color: ${colors.text};
    background-color: ${colors.textPrimary};
  }
`

export const UserActionsContainer = styled.div`
  display: flex;
  gap:;
`

export const CreatePost = styled.button`
  box-sizing: border-box;
  color: ${colors.textPrimary};
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 500;
  font-size: 20px;
  line-height: 24px;
  padding: 7px 12px;
  background: transparent;
  cursor: pointer;

  border-radius: 10px;
  border: 1px solid ${colors.textPrimary};

  transition: all 0.25s ease-in;

  &:hover {
    background: ${colors.textPrimary};
    color: ${colors.text};
  }
`

export const UserAvatar = styled.img`
  height: 42px;
  width: 42px;
  border-radius: 50%;

  cursor: pointer;
  object-fit: cover;
`

export const Logout = styled.button`
  background: transparent;
  background-image: url(${logoutIcon});
  border: none;
  height: 34px;
  width: 44px;

  object-fit: cover;
  cursor: pointer;
`

export const NotificationImgContainer = styled.div<{ $count: number }>`
  position: relative;
  cursor: pointer;
  height: 24px;
  width: 24px;

  &:after {
    content: '${({ $count }) => ($count === 0 ? '' : $count > 9 ? '9+' : $count)}';
    position: absolute;
    display: ${({ $count }) => ($count === 0 ? 'none' : 'flex')};
    justify-content: center;
    align-items: center;
    width: ${({ $count }) => ($count > 9 ? '18px' : '14px')};
    height: ${({ $count }) => ($count > 9 ? '18px' : '14px')};
    background-color: #ff9900;
    border-radius: 50%;
    top: -5px;
    right: -2px;
    z-index: 500;
    font-size: 12px;
    color: #fff;
    font-weight: bold;
    padding: 0 4px;
    box-sizing: border-box;
  }
`

export const NotificationImg = styled.img`
  height: 100%;
  width: 100%;
  object-fit: cover;
`

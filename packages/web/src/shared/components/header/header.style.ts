import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'
import { Link } from 'react-router-dom'
import styled from 'styled-components'

export const Wrapper = styled.div`
  background-color: ${colors.background};
  margin-bottom: 20px;
`

export const Container = styled.div`
  max-width: 1440px;
  width: 100%;
  margin: 0 auto;

  display: grid;
  grid-template-columns: 300px minmax(300px, 1fr) 300px;
  align-items: center;
`

export const Logo = styled.img`
  padding: 10px 0;
`

export const NavList = styled.nav`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 30px;
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

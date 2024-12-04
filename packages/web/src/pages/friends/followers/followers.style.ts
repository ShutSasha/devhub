import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'
import { Link } from 'react-router-dom'
import { colors } from '@shared/consts/colors.const'

export const ChangeList = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-bottom: 16px;
`

export const ListText = styled(Link)<{ $underline: boolean }>`
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 500;
  line-height: 20px;
  font-size: 16px;
  color: #fff;
  text-decoration: ${({ $underline }) => ($underline ? 'underline' : 'none')};
`

export const UsersList = styled.div`
  display: flex;
  flex-direction: column;
`

export const UserListItem = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-top: 1px solid #2a2a2d;
  border-bottom: 1px solid #2a2a2d;
`

export const UserListItemDataContainer = styled.div`
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 16px;
`

export const UserAvatar = styled.img`
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
`

export const UserName = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 500;
  line-height: 20px;
  font-size: 16px;
  color: ${colors.textPrimary};
`

export const OpenChatIcon = styled.img`
  width: 24px;
  height: 24px;
  cursor: pointer;
`

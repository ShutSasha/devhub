import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'
import { Link } from 'react-router-dom'
import { colors } from '@shared/consts/colors.const'
import checkNotification from '@assets/images/notification/checked.svg'
import checkNotificationFilled from '@assets/images/notification/checked-filled.svg'

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

  &: last-child {
    border-bottom: none;
  }
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

export const ReadNotificationIcon = styled.div`
  width: 24px;
  height: 24px;
  background-image: url(${checkNotification});
  background-size: cover;
  cursor: pointer;
  transition: background-image 0.3s;

  &:hover {
    background-image: url(${checkNotificationFilled});
  }
`

export const UnFollowButton = styled.button`
  background-color: transparent;
  border: ${colors.textPrimary} 1px solid;
  cursor: pointer;
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 500;
  line-height: 20px;
  font-size: 16px;
  color: ${colors.textPrimary};
  transition: color 0.3s;
  border-radius: 5px;
  padding: 4px 10px;

  &:hover {
    color: #000;
    background-color: ${colors.primaryOrange};
    border: 1px solid transparent;
  }
`

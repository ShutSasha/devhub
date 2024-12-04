import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'

export const UserProfileInfoContainer = styled.div`
  position: relative;
  background-color: #2a2a2d;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding-top: 74px;
  padding-bottom: 48px;
  margin-top: 86px;
  border-radius: 15px;
  margin-bottom: 18px;
`

export const UserAvatat = styled.img`
  position: absolute;
  top: 0%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 110px;
  height: 110px;
  border-radius: 50%;
  border: 5px solid ${colors.bgPage};
  object-fit: cover;
`

export const Username = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 32px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 5px;
`

export const Name = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 20px;
  font-weight: 500;
  color: #fff;
  margin-bottom: 6px;
`

export const UserDescription = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 20px;
  font-weight: 400;
  color: #fff;
  margin-bottom: 10px;
`

export const UserFollowersText = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 20px;
  font-weight: 400;
  color: #fff;
  margin-bottom: 10px;
`

export const EditProfileBtn = styled.button`
  background-color: #626266;
  color: #fff;
  font-size: 16px;
  font-weight: 500;
  font-family: ${FONTS.MONTSERRAT};
  padding: 6px 12px;
  border-radius: 10px;
`

export const InterectWithUserContainer = styled.div`
  display: flex;
  gap: 18px;
  align-items: center;
  margin-bottom: 10px;
`

export const FollowBtn = styled.button`
  background-color: #f7971d;
  color: #fff;
  font-size: 16px;
  font-weight: 500;
  font-family: ${FONTS.MONTSERRAT};
  padding: 6px 12px;
  border-radius: 5px;
  transition: all 0.2s ease-out;
  border: 1px solid transparent;

  &:hover {
    border: 1px solid #f7971d;
    background-color: transparent;
    color: #f7971d;
  }
`

export const UnfollowBtn = styled(FollowBtn)`
  background-color: transparent;
  color: #f7971d;
  border: 1px solid #f7971d;

  &:hover {
    background-color: #f7971d;
    color: #fff;
  }
`

export const SendMessageBtn = styled.button`
  background-color: transparent;
  color: #f7971d;
  font-size: 16px;
  font-weight: 500;
  font-family: ${FONTS.MONTSERRAT};
  padding: 6px 12px;
  border-radius: 5px;
  border: 1px solid #f7971d;
  transition: all 0.2s ease-out;

  &:hover {
    background-color: #f7971d;
    color: #fff;
  }
`

export const UserProfileContentContainer = styled.div`
  display: grid;
  gap: 20px;
  grid-template-columns: 1fr 2fr;
  align-items: start;
`

export const UserCountAchivmentContainer = styled.div`
  background-color: #2a2a2d;
  padding: 30px 18px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  border-radius: 15px;
`
export const UserCountAchivmentInnerContainer = styled.div`
  display: flex;
  gap: 18px;
`

export const CountAchivmentText = styled.p`
  color: #fff;
  font-size: 16px;
  font-family: ${FONTS.INTER};
  font-weight: 400;
`

export const UserActivityContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 18px;
  margin-bottom: 20px;
`

// Post

export const PostContainer = styled.div`
  background-color: #2a2a2d;
  border-radius: 15px;
  padding: 12px;
  cursor: pointer;
`

export const PostHeaderContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 22px;
  margin-bottom: 12px;
`

export const PostUserAvatar = styled.img`
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
`

export const PostUsername = styled.p`
  font-size: 20px;
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 600;
  color: #fff;
`

export const PostTitle = styled.p`
  font-size: 32px;
  font-family: ${FONTS.INTER};
  font-weight: 700;
  color: ${colors.textPrimary};
  margin-bottom: 4px;
`

export const HeaderImage = styled.img`
  max-height: 400px;
  height: 100%;
  width: 100%;

  object-fit: cover;
  object-position: 50% 0%;
  box-sizing: border-box;
  border-radius: 8px;
  margin-bottom: 18px;
`

export const PostContent = styled.p`
  font-size: 20px;
  font-family: ${FONTS.INTER};
  font-weight: 700;
  color: #fff;
`

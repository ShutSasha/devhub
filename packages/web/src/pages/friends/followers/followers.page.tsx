import { SearchInput } from '@shared/components/search-input/search-input.component'
import { MainLayout } from '@shared/layouts/main.layout'
import { ROUTES } from '@pages/router/routes.enum'
import { useAppSelector } from '@app/store/store'
import { useNavigate, useParams } from 'react-router-dom'
import openChatSvg from '@assets/images/chat/open-chat.svg'
import { useGetUserFollowersQuery } from '@api/user.api'

import * as _ from './followers.style'

export const Followers = () => {
  const { id } = useParams()
  const { data: followers, isLoading } = useGetUserFollowersQuery({ userId: id })
  const navigate = useNavigate()
  const user = useAppSelector(state => state.userSlice.user)
  const currentUrl = window.location.href

  const handleRedirectToUserProfile = (id: string) => {
    navigate(ROUTES.USER_PROFILE.replace(':id', id))
  }

  if (!user) {
    return null
  }

  return (
    <MainLayout>
      <div></div>
      <div>
        <SearchInput isChatSearch={false} placeholder="Search by users..." />
        <_.ChangeList>
          <_.ListText
            $underline={currentUrl.includes(ROUTES.USER_FRIENDS_FOLLOWERS.replace(':id', ''))}
            to={ROUTES.USER_FRIENDS_FOLLOWERS.replace(':id', user._id)}
          >
            Followers
          </_.ListText>
          <_.ListText
            $underline={currentUrl.includes(ROUTES.USER_FRIENDS_FOLLOWED_LIST.replace(':id', ''))}
            to={ROUTES.USER_FRIENDS_FOLLOWED_LIST.replace(':id', user._id)}
          >
            Followed
          </_.ListText>
        </_.ChangeList>
        {isLoading && <p>Loading...</p>}
        <_.UsersList>
          {followers &&
            followers.map(follower => (
              <_.UserListItem key={follower._id}>
                <_.UserListItemDataContainer onClick={() => handleRedirectToUserProfile(follower._id)}>
                  <_.UserAvatar src={follower.avatar} alt="avatar" />
                  <_.UserName>{follower.username}</_.UserName>
                </_.UserListItemDataContainer>
                <_.OpenChatIcon src={openChatSvg} alt="open chat" />
              </_.UserListItem>
            ))}
        </_.UsersList>
      </div>
      <div></div>
    </MainLayout>
  )
}

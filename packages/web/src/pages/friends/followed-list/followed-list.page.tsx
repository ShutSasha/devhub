import { toast } from 'react-toastify'
import { useAppSelector } from '@app/store/store'
import { ROUTES } from '@pages/router/routes.enum'
import { SearchInput } from '@shared/components/search-input/search-input.component'
import { MainLayout } from '@shared/layouts/main.layout'
import { useNavigate, useParams } from 'react-router-dom'
import openChatSvg from '@assets/images/chat/open-chat.svg'
import { useDeleteUserFollowingMutation, useGetUserFollowingsQuery } from '@api/user.api'
import { handleServerException } from '@utils/handleServerException.util'

import * as _ from '../followers/followers.style'

import { ErrorException } from '~types/error/error.type'

export const FollowedList = () => {
  const { id } = useParams()
  const {
    data: followings,
    isLoading,
    refetch,
  } = useGetUserFollowingsQuery({ userId: id }, { refetchOnMountOrArgChange: true })
  const [unfollow] = useDeleteUserFollowingMutation()
  const navigate = useNavigate()
  const user = useAppSelector(state => state.userSlice.user)
  const currentUrl = window.location.href

  const handleRedirectToUserProfile = (id: string) => {
    navigate(ROUTES.USER_PROFILE.replace(':id', id))
  }

  const handleUnfollow = async (followingId: string) => {
    try {
      await unfollow({ userId: user?._id, followingUserId: followingId }).unwrap()

      refetch()
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    }
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
          {followings &&
            followings.map(following => (
              <_.UserListItem key={following._id}>
                <_.UserListItemDataContainer onClick={() => handleRedirectToUserProfile(following._id)}>
                  <_.UserAvatar src={following.avatar} alt="avatar" />
                  <_.UserName>{following.username}</_.UserName>
                </_.UserListItemDataContainer>
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                  <_.OpenChatIcon src={openChatSvg} alt="open chat" />
                  <_.UnFollowButton onClick={() => handleUnfollow(following._id)}>Unfollow</_.UnFollowButton>
                </div>
              </_.UserListItem>
            ))}
        </_.UsersList>
      </div>
      <div></div>
    </MainLayout>
  )
}

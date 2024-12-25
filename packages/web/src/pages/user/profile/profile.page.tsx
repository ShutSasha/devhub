import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { useNavigate, useParams } from 'react-router-dom'
import { HubConnection, HubConnectionBuilder } from '@microsoft/signalr'
import { UserProfileLayout } from '@shared/layouts/user/user-profile.layout'
import {
  useAddFollowingUserFollowingMutation,
  useDeleteUserFollowingMutation,
  useGetUserDetailsQuery,
} from '@api/user.api'
import postCreatedSVG from '@assets/images/user/post-created-icon.svg'
import commentWrittenSVG from '@assets/images/user/comment-written-icon.svg'
import { ROUTES } from '@pages/router/routes.enum'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { handleServerException } from '@utils/handleServerException.util'
import { setActiveChatId } from '@features/chat/chat.slice'

import * as _ from './profile.style'

import { ErrorException } from '~types/error/error.type'

export const UserProfile = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const user = useAppSelector(state => state.userSlice.user)
  const {
    data: userDetails,
    isLoading,
    refetch,
  } = useGetUserDetailsQuery({ userId: id }, { refetchOnMountOrArgChange: true })
  const [followByUser] = useAddFollowingUserFollowingMutation()
  const [unfollowByUser] = useDeleteUserFollowingMutation()
  const [connection, setConnection] = useState<HubConnection | null>(null)
  const dispatch = useAppDispatch()

  useEffect(() => {
    const newConnection = new HubConnectionBuilder()
      .withUrl('http://localhost:5231/chat')
      .withAutomaticReconnect()
      .build()

    setConnection(newConnection)
    return () => {
      if (newConnection) {
        newConnection.stop()
      }
    }
  }, [])

  const handleFollow = async () => {
    try {
      await followByUser({ userId: user?._id, followingUserId: userDetails?._id }).unwrap()

      refetch()
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    }
  }

  const handleUnfollow = async () => {
    try {
      await unfollowByUser({ userId: user?._id, followingUserId: userDetails?._id }).unwrap()

      refetch()
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    }
  }

  const handleRedirectToChat = async (follower_id: string) => {
    if (user) {
      try {
        await connection?.start()

        if (user._id && follower_id) await connection?.invoke('JoinChat', user._id, follower_id)

        connection?.on('JoinedChat', (chatId: string) => {
          dispatch(setActiveChatId(chatId))
        })

        await connection?.invoke('JoinChat', user._id, follower_id)
        navigate(ROUTES.CHAT.replace(':id', user._id))
      } catch (error) {
        console.error('Error in handleRedirectToChat:', error)
      }
    }
  }

  if (!id) {
    return (
      <UserProfileLayout>
        <p>loading or error</p>
      </UserProfileLayout>
    )
  }

  if (isLoading) {
    return (
      <UserProfileLayout>
        <p>Loading...</p>
      </UserProfileLayout>
    )
  }

  if (!userDetails) {
    return (
      <UserProfileLayout>
        <p>user not found</p>
      </UserProfileLayout>
    )
  }

  return (
    <UserProfileLayout>
      <_.UserProfileInfoContainer>
        <_.UserAvatat src={userDetails.avatar} />
        <_.Username>{userDetails.username}</_.Username>
        {userDetails.name && <_.Name>{userDetails.name}</_.Name>}
        {userDetails.bio && <_.UserDescription>{userDetails.bio}</_.UserDescription>}
        {
          <_.UserFollowersText>
            {userDetails?.followers ? userDetails.followers.length : 0} followers
          </_.UserFollowersText>
        }
        {userDetails._id !== user?._id && (
          <_.InterectWithUserContainer>
            {user && userDetails?.followers?.includes(user?._id) ? (
              <_.UnfollowBtn onClick={handleUnfollow}>Unfollow</_.UnfollowBtn>
            ) : (
              <_.FollowBtn onClick={handleFollow}>Follow</_.FollowBtn>
            )}
            <_.SendMessageBtn onClick={() => handleRedirectToChat(id)}>Send message</_.SendMessageBtn>
          </_.InterectWithUserContainer>
        )}
        {user?._id === userDetails._id && (
          <_.EditProfileBtn onClick={() => navigate(`${ROUTES.USER_EDIT_PROFILE.replace(':id', userDetails._id)}`)}>
            Edit profile
          </_.EditProfileBtn>
        )}
      </_.UserProfileInfoContainer>
      <_.UserProfileContentContainer>
        <_.UserCountAchivmentContainer>
          <_.UserCountAchivmentInnerContainer>
            <img src={postCreatedSVG} />
            <_.CountAchivmentText>{userDetails.posts.length} post published</_.CountAchivmentText>
          </_.UserCountAchivmentInnerContainer>
          <_.UserCountAchivmentInnerContainer>
            <img src={commentWrittenSVG} />
            <_.CountAchivmentText>{userDetails.comments.length} comments written</_.CountAchivmentText>
          </_.UserCountAchivmentInnerContainer>
        </_.UserCountAchivmentContainer>
        <_.UserActivityContainer>
          {userDetails.posts &&
            userDetails.posts.map(post => (
              <_.PostContainer key={post._id} onClick={() => navigate(`${ROUTES.POST_VIEW.replace(':id', post._id)}`)}>
                <_.PostHeaderContainer>
                  <_.PostUserAvatar src={userDetails.avatar} />
                  <_.PostUsername>@{userDetails.username}</_.PostUsername>
                </_.PostHeaderContainer>
                {post.headerImage && (
                  <_.HeaderImage src={'https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/' + post.headerImage} />
                )}
                <_.PostTitle>{post.title}</_.PostTitle>
                <_.PostContent>{post.content}</_.PostContent>
              </_.PostContainer>
            ))}
        </_.UserActivityContainer>
      </_.UserProfileContentContainer>
    </UserProfileLayout>
  )
}

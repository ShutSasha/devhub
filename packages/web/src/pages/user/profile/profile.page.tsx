import { useNavigate, useParams } from 'react-router-dom'
import { UserProfileLayout } from '@shared/layouts/user/user-profile.layout'
import { useGetUserDetailsQuery } from '@api/user.api'
import postCreatedSVG from '@assets/images/user/post-created-icon.svg'
import commentWrittenSVG from '@assets/images/user/comment-written-icon.svg'
import { ROUTES } from '@pages/router/routes.enum'
import { useAppSelector } from '@app/store/store'

import * as _ from './profile.style'

export const UserProfile = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const user = useAppSelector(state => state.userSlice.user)
  const { data: userDetails, isLoading } = useGetUserDetailsQuery({ userId: id }, { refetchOnMountOrArgChange: true })

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

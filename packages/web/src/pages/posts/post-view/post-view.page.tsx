import { useGetPostByIdQuery } from '@api/post.api'
import { StyledAvatar, StyledUserCredentialsContainer, Username } from '@shared/components/post/post.style'
import { PostViewLayout } from '@shared/layouts/posts/post-view.layout'
import { useParams } from 'react-router-dom'

export const PostView = () => {
  const { id } = useParams()
  const { data: post, error, isLoading } = useGetPostByIdQuery({ id })

  if (error) {
    return (
      <PostViewLayout>
        <p>something went wrong</p>
      </PostViewLayout>
    )
  }

  if (isLoading) {
    return (
      <PostViewLayout>
        <p>loading</p>
      </PostViewLayout>
    )
  }

  if (!post) {
    return (
      <PostViewLayout>
        <p>not found post</p>
      </PostViewLayout>
    )
  }

  return (
    <PostViewLayout>
      <StyledUserCredentialsContainer style={{ marginLeft: '20px', marginBottom: '20px' }}>
        <StyledAvatar style={{ height: '60px', width: '60px' }} src={post.user.avatar} />
        <Username style={{ fontSize: '26px', lineHeight: '36px', fontWeight: '500' }}>{post.user.username}</Username>
      </StyledUserCredentialsContainer>
    </PostViewLayout>
  )
}

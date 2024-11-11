import { useGetPostByIdQuery } from '@api/post.api'
import { StyledAvatar, StyledUserCredentialsContainer, Username } from '@shared/components/post/post.style'
import { PostViewLayout } from '@shared/layouts/posts/post-view.layout'
import { useParams } from 'react-router-dom'
import { parseDate } from '@utils/parseDate.util'
import { parseTagsToUI } from '@utils/parseTagsToUI.util'

import { ContentText, PostCreationData, PostImage, PostTag, PostTagsContainer, PostTitle } from './post-view.style'

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
      <StyledUserCredentialsContainer style={{ marginBottom: '20px' }}>
        <StyledAvatar style={{ height: '60px', width: '60px' }} src={post.user.avatar} />
        <Username style={{ fontSize: '26px', lineHeight: '36px', fontWeight: '500' }}>{post.user.username}</Username>
      </StyledUserCredentialsContainer>
      <PostTitle>{post.title}</PostTitle>
      {post.headerImage && (
        <PostImage src={'https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/' + post.headerImage} />
      )}
      <PostCreationData>{parseDate(post.createdAt)}</PostCreationData>
      {post.tags && (
        <PostTagsContainer>
          {parseTagsToUI(post.tags).map((tag, index) => (
            <PostTag key={index}>{tag}</PostTag>
          ))}
        </PostTagsContainer>
      )}
      <ContentText>{post.content}</ContentText>
    </PostViewLayout>
  )
}

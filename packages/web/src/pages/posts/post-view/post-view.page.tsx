import { useGetPostByIdQuery } from '@api/post.api'
import { StyledAvatar, StyledUserCredentialsContainer, Username } from '@shared/components/post/post.style'
import { PostViewLayout } from '@shared/layouts/posts/post-view.layout'
import { useParams } from 'react-router-dom'
import { parseDate } from '@utils/parseDate.util'
import { parseTagsToUI } from '@utils/parseTagsToUI.util'
import { useRef } from 'react'

import {
  ActionContainer,
  ActionInnerContainer,
  Comment,
  ContentText,
  Dislike,
  GrayLine,
  Like,
  PostBtn,
  PostCreationData,
  PostImage,
  PostTag,
  PostTagsContainer,
  PostTitle,
  WriteCommentContainer,
} from './post-view.style'
import { InputContainer } from './components/write-comment.component'

export const PostView = () => {
  const { id } = useParams()
  const { data: post, error, isLoading } = useGetPostByIdQuery({ id })
  const comment = useRef<HTMLSpanElement>(null)

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
      <GrayLine />
      <ActionContainer>
        <ActionInnerContainer>
          <Like />
          <p>{post.likes} likes</p>
        </ActionInnerContainer>
        <ActionInnerContainer>
          <Dislike />
          <p>{post.likes} likes</p>
        </ActionInnerContainer>
        <ActionInnerContainer></ActionInnerContainer>
        <ActionInnerContainer>
          <Comment />
          <p>{post.comments.length} comments</p>
        </ActionInnerContainer>
      </ActionContainer>
      <WriteCommentContainer>
        <StyledAvatar style={{ height: '40px', width: '40px' }} src={post.user.avatar} />
        <InputContainer ref={comment} />
        <PostBtn>Post</PostBtn>
      </WriteCommentContainer>
    </PostViewLayout>
  )
}

import { useRef } from 'react'
import { useGetPostByIdQuery } from '@api/post.api'
import { StyledAvatar, StyledUserCredentialsContainer, Username } from '@shared/components/post/post.style'
import { PostViewLayout } from '@shared/layouts/posts/post-view.layout'
import { useParams } from 'react-router-dom'
import { parseDate } from '@utils/parseDate.util'
import { parseTagsToUI } from '@utils/parseTagsToUI.util'
import { useAppSelector } from '@app/store/store'
import { useCreateCommentMutation } from '@api/comment.api'
import { handleServerException } from '@utils/handleServerException.util'
import { toast } from 'react-toastify'

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
import { Comment as CommentComponent } from './components/comment.component'
import { InputContainer } from './components/write-comment.component'

import { ErrorException } from '~types/error/error.type'

export const PostView = () => {
  const { id } = useParams()
  const { data: post, error, isLoading, refetch } = useGetPostByIdQuery({ id })
  const user = useAppSelector(state => state.userSlice.user)
  const comment = useRef<HTMLSpanElement>(null)
  const [createComment] = useCreateCommentMutation()

  const handlePostComment = async () => {
    try {
      const response = await createComment({
        content: comment.current?.textContent,
        postId: id,
        userId: user?._id,
      }).unwrap()

      if (response) refetch()
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    }
  }

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
        <StyledAvatar src={user?.avatar || 'https://i.pinimg.com/736x/ed/af/52/edaf52b72775ebb5d6fa004bed32526b.jpg'} />
        <InputContainer ref={comment} />
        <PostBtn onClick={handlePostComment}>Post</PostBtn>
      </WriteCommentContainer>

      {Array.isArray(post.comments) &&
        post.comments.map(comment => <CommentComponent key={comment._id} comment={comment} />)}
    </PostViewLayout>
  )
}

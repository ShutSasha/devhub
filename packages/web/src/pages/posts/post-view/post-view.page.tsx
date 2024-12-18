import { MouseEvent, useEffect, useRef, useState } from 'react'
import { useDeletePostMutation, useDislikeMutation, useGetPostByIdQuery, useLikeMutation } from '@api/post.api'
import {
  StyledAvatar,
  StyledUserCredentialsContainer,
  StyledUserCredentialsContainerWrapper,
  Username,
} from '@shared/components/post/post.style'
import { PostViewLayout } from '@shared/layouts/posts/post-view.layout'
import { useNavigate, useParams } from 'react-router-dom'
import { parseDate } from '@utils/parseDate.util'
import { parseTagsToUI } from '@utils/parseTagsToUI.util'
import { useAppSelector } from '@app/store/store'
import { useCreateCommentMutation } from '@api/comment.api'
import { handleServerException } from '@utils/handleServerException.util'
import { toast } from 'react-toastify'
import { ROUTES } from '@pages/router/routes.enum'
import { useGetUserReactionsQuery, useLazyGetReportsByUserQuery, useReportPostMutation } from '@api/user.api'
import editPostSVG from '@assets/images/post/edit-post-icon.svg'
import deletePostSVG from '@assets/images/post/delete-post-ic.svg'
import reportSVG from '@assets/images/post/report.svg'

import {
  ActionContainer,
  ActionInnerContainer,
  CategoryButton,
  Comment,
  ContentText,
  Dislike,
  GrayLine,
  Like,
  Modal,
  Overlay,
  PostBtn,
  PostCreationData,
  PostImage,
  PostTag,
  PostTagsContainer,
  PostTitle,
  ReportIcon,
  Title,
  WriteCommentContainer,
} from './post-view.style'
import { Comment as CommentComponent } from './components/comment.component'
import { InputContainer } from './components/write-comment.component'

import { ErrorException } from '~types/error/error.type'
import { IReport } from '~types/post/post.type'

export const PostView = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const { data: post, error, isLoading, refetch } = useGetPostByIdQuery({ id })
  const user = useAppSelector(state => state.userSlice.user)
  const comment = useRef<HTMLSpanElement>(null)
  const [createComment] = useCreateCommentMutation()
  const [deletePost] = useDeletePostMutation()
  const { data: userReactions, refetch: refetchReactions } = useGetUserReactionsQuery({ userId: user?._id })
  const [like] = useLikeMutation()
  const [dislike] = useDislikeMutation()
  const [isLiked, setLiked] = useState<boolean>(false)
  const [isDisliked, setDisliked] = useState<boolean>(false)
  const [isDisableBtn, setDisabledBtn] = useState<boolean>(false)
  const [isModalVisible, setModalVisible] = useState(false)
  const [isClosing, setIsClosing] = useState(false)
  const [reportPost] = useReportPostMutation()
  const [getReportsByUser] = useLazyGetReportsByUserQuery()

  useEffect(() => {
    refetchReactions()
    refetch()
    if (post && userReactions) {
      setLiked(userReactions.likedPosts.includes(post._id))
      setDisliked(userReactions.dislikedPosts.includes(post._id))
    }
  }, [post, userReactions])

  const handleLikeClick = async (e: MouseEvent<HTMLElement>) => {
    try {
      e.stopPropagation()
      if (!user) return toast.error('Log in for leaving reactions')
      if (post) {
        await like({ postId: post._id, userId: user?._id }).unwrap()
        refetch()
        refetchReactions()
      }
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    }
  }

  const handleDislikeClick = async (e: MouseEvent<HTMLElement>) => {
    try {
      e.stopPropagation()
      if (!user) return toast.error('Log in for leaving reactions')
      if (post) {
        await dislike({ postId: post._id, userId: user?._id }).unwrap()
        refetch()
        refetchReactions()
      }
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    }
  }

  const handlePostComment = async () => {
    try {
      setDisabledBtn(true)
      const response = await createComment({
        content: comment.current?.textContent,
        postId: id,
        userId: user?._id,
      }).unwrap()

      if (comment.current) {
        comment.current.textContent = null
        const event = new Event('input', { bubbles: true })
        comment.current.dispatchEvent(event)
      }

      if (response) refetch()
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    } finally {
      setDisabledBtn(false)
    }
  }

  const handleDeletePost = async () => {
    try {
      await deletePost({ id }).unwrap()
      toast.success('Post has been deleted', { autoClose: 1300 })

      navigate(ROUTES.HOME)
      window.scrollTo(0, 0)
    } catch (e) {
      console.error(e)
    }
  }

  const handleReportPost = async (category: string) => {
    try {
      if (!user) return toast.error('Log in for reporting posts', { autoClose: 1300 })

      const reports = await getReportsByUser({ userId: user._id }).unwrap()

      const isReported = reports.some((report: IReport) => report.content === post?._id)

      if (isReported) return toast.error('You have already reported this post', { autoClose: 1300 })

      await reportPost({ sender: user._id, content: post?._id, category }).unwrap()

      toast.info(`Post reported for ${category}`, { autoClose: 1300 })
      closeModal()
    } catch (e) {
      console.error(e)
    }
  }

  const openModal = () => {
    setIsClosing(false)
    setModalVisible(true)
  }

  const closeModal = () => {
    setIsClosing(true)
    setTimeout(() => setModalVisible(false), 500) // 500ms відповідає тривалості анімації
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
      <StyledUserCredentialsContainerWrapper>
        <StyledUserCredentialsContainer style={{ marginBottom: '20px' }}>
          <StyledAvatar
            onClick={() => navigate(ROUTES.USER_PROFILE.replace(':id', post.user._id))}
            style={{ height: '60px', width: '60px', cursor: 'pointer' }}
            src={post.user.avatar}
          />
          <Username style={{ fontSize: '26px', lineHeight: '36px', fontWeight: '500' }}>{post.user.username}</Username>
        </StyledUserCredentialsContainer>
        <div style={{ display: 'flex', alignSelf: 'flex-start', gap: '12px' }}>
          {user?._id === post.user._id && (
            <div style={{ display: 'flex', gap: '12px' }}>
              <img
                onClick={() => navigate(ROUTES.EDIT_POST.replace(':id', post._id))}
                style={{ cursor: 'pointer' }}
                src={editPostSVG}
              />
              <img
                onClick={handleDeletePost}
                style={{ alignSelf: 'flex-start', cursor: 'pointer' }}
                src={deletePostSVG}
              />
            </div>
          )}
          <ReportIcon onClick={openModal} src={reportSVG} />
          {isModalVisible && (
            <Overlay className={isClosing ? 'hidden' : 'visible'} onClick={closeModal}>
              <Modal className={isClosing ? 'hidden' : 'visible'} onClick={e => e.stopPropagation()}>
                <Title>Report this post by one category</Title>
                <CategoryButton onClick={() => handleReportPost('Spam')}>Spam</CategoryButton>
                <CategoryButton onClick={() => handleReportPost('Copyright Infringement')}>
                  Copyright Infringement
                </CategoryButton>
                <CategoryButton onClick={() => handleReportPost('Misinformation')}>Misinformation</CategoryButton>
              </Modal>
            </Overlay>
          )}
        </div>
      </StyledUserCredentialsContainerWrapper>
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
          <Like $isLiked={isLiked} onClick={handleLikeClick} />
          <p>{post.likes} likes</p>
        </ActionInnerContainer>
        <ActionInnerContainer>
          <Dislike $isDisliked={isDisliked} onClick={handleDislikeClick} />
          <p>{post.dislikes} dislikes</p>
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
        <PostBtn disabled={isDisableBtn} onClick={handlePostComment}>
          Post
        </PostBtn>
      </WriteCommentContainer>

      {Array.isArray(post.comments) &&
        post.comments.map(comment => <CommentComponent key={comment._id} comment={comment} />)}
    </PostViewLayout>
  )
}

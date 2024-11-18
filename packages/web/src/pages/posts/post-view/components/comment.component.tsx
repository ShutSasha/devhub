import { FC } from 'react'
import { StyledAvatar } from '@shared/components/post/post.style'
import { parseDate } from '@utils/parseDate.util'
import { useDeleteCommentByIdMutation } from '@api/comment.api'
import { handleServerException } from '@utils/handleServerException.util'
import { toast } from 'react-toastify'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { api } from '@api/post.api'

import {
  CommentContainer,
  CommentHeaderContainer,
  CommentHeaderInnerContainer,
  CommentInnerContainer,
  CommentPostedDate,
  CommentText,
  RemoveBtn,
  Username,
} from './comment.style'

import { IComment } from '~types/comment/comment.type'
import { ErrorException } from '~types/error/error.type'

interface CommentProps {
  comment: IComment
}

export const Comment: FC<CommentProps> = ({ comment }) => {
  const userId = useAppSelector(state => state.userSlice.user?._id)
  const dispatch = useAppDispatch()
  const [deleteComment] = useDeleteCommentByIdMutation()

  const handleDeleteComment = async () => {
    try {
      const response = await deleteComment({ id: comment._id }).unwrap()
      if (response) {
        dispatch(
          api.util.updateQueryData('getPostById', { id: comment.post }, (draft: any) => {
            const commentIndex = draft.comments.findIndex((c: any) => c._id === comment._id)
            if (commentIndex !== -1) {
              draft.comments.splice(commentIndex, 1)
            }
          }),
        )
        toast.success('Comment deleted successfully!')
      }
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    }
  }

  return (
    <CommentContainer>
      <StyledAvatar style={{ height: '40px', width: '40px' }} src={comment.user.avatar} />
      <CommentInnerContainer>
        <CommentHeaderContainer>
          <CommentHeaderInnerContainer>
            <Username>{comment.user.username}</Username>
            <CommentPostedDate>{parseDate(comment.createdAt)}</CommentPostedDate>
          </CommentHeaderInnerContainer>
          {userId === comment.user._id ? <RemoveBtn onClick={handleDeleteComment}>Delete</RemoveBtn> : ''}
        </CommentHeaderContainer>
        <CommentText>{comment.commentText}</CommentText>
      </CommentInnerContainer>
    </CommentContainer>
  )
}

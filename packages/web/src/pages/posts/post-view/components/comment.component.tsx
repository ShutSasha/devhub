import { FC } from 'react'
import { StyledAvatar } from '@shared/components/post/post.style'
import { parseDate } from '@utils/parseDate.util'

import {
  CommentContainer,
  CommentHeaderContainer,
  CommentInnerContainer,
  CommentPostedDate,
  CommentText,
  Username,
} from './comment.style'

import { IComment } from '~types/comment/comment.type'

interface CommentProps {
  comment: IComment
}

export const Comment: FC<CommentProps> = ({ comment }) => {
  return (
    <CommentContainer>
      <StyledAvatar style={{ height: '40px', width: '40px' }} src={comment.user.avatar} />
      <CommentInnerContainer>
        <CommentHeaderContainer>
          <Username>{comment.user.username}</Username>
          <CommentPostedDate>{parseDate(comment.createdAt)}</CommentPostedDate>
        </CommentHeaderContainer>
        <CommentText>{comment.commentText}</CommentText>
      </CommentInnerContainer>
    </CommentContainer>
  )
}

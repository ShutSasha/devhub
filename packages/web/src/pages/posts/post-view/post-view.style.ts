import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'
import like from '@assets/images/post/likes/like.svg'
import likeHover from '@assets/images/post/likes/like-hover.svg'
import likePressed from '@assets/images/post/likes/like-pressed.svg'
import dislike from '@assets/images/post/dislikes/dislike.svg'
import dislikeHover from '@assets/images/post/dislikes/dislike-hover.svg'
import dislikePressed from '@assets/images/post/dislikes/dislike-pressed.svg'
import comment from '@assets/images/post/comments/comment.svg'
import commentHover from '@assets/images/post/comments/comment-hover.svg'
import { colors } from '@shared/consts/colors.const'

export const PostTitle = styled.p`
  font-size: 20px;
  line-height: 24px;
  font-family: ${FONTS.INTER};
  font-weight: 700;
  margin-bottom: 18px;
`

export const PostImage = styled.img`
  max-height: 500px;
  height: 100%;
  width: 100%;

  object-fit: cover;
  object-position: 50% 50%;
  box-sizing: border-box;
  border-radius: 8px;
  margin-bottom: 18px;
`

export const PostCreationData = styled.p`
  font-size: 16px;
  line-height: 20px;
  font-family: ${FONTS.INTER};
  color: #ededed7d;
  font-weight: 700;
  margin-bottom: 18px;
`

export const PostTagsContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
`

export const PostTag = styled.p`
  font-size: 16px;
  line-height: 20px;
  font-family: ${FONTS.INTER};
  font-weight: 700;
`

export const ContentText = styled.p`
  font-size: 16px;
  line-height: 24px;
  font-family: ${FONTS.INTER};
  color: #fff;
  font-weight: 500;
  text-justify: inter-word;
  text-align: justify;
  margin-bottom: 18px;
`

export const GrayLine = styled.hr`
  height: 1px;
  width: 100%;
  background-color: #ffffff66;
  border: none;
  margin-bottom: 18px;
`

export const ActionContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
`

export const ActionInnerContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
`

export const ReactionImage = styled.div`
  height: 20px;
  width: 20px;
  cursor: pointer;

  background-size: 100% 100%;
  background-position: center;
  box-sizing: border-box;
`

interface StyledLikeProps {
  $isLiked?: boolean
}

export const Like = styled(ReactionImage)<StyledLikeProps>`
  background-image: ${({ $isLiked }) => ($isLiked ? `url(${likePressed})` : `url(${like})`)};
  transition: all 0.1s ease-in;

  &:hover {
    background-image: ${({ $isLiked }) => ($isLiked ? `url(${likePressed})` : `url(${likeHover})`)};
  }
`

interface StyledDislikeProps {
  $isDisliked?: boolean
}

export const Dislike = styled(ReactionImage)<StyledDislikeProps>`
  background-image: ${({ $isDisliked }) => ($isDisliked ? `url(${dislikePressed})` : `url(${dislike})`)};
  transition: all 0.1s ease-in;

  &:hover {
    background-image: ${({ $isDisliked }) => ($isDisliked ? `url(${dislikePressed})` : `url(${dislikeHover})`)};
  }
`

export const Comment = styled(ReactionImage)`
  background-image: url(${comment});
  transition: all 0.1s ease-in;

  &:hover {
    background-image: url(${commentHover});
  }
`

export const WriteCommentContainer = styled.div`
  display: flex;
  justify-content: space-between;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid ${colors.white300};
  gap: 20px;
  margin-bottom: 12px;
`

export const PostBtn = styled.button`
  background-color: #f7971d;
  font-family: ${FONTS.MONTSERRAT};
  color: #fff;
  font-size: 16px;
  line-height: 20px;
  padding: 14px 28px;
  border-radius: 5px;
  align-self: flex-start;
  cursor: pointer;
  transition: all 0.25s ease-in;
  box-sizing: border-box;
  border: 1px solid transparent;

  &:hover {
    background-color: transparent;
    border: 1px solid #f7971d;
    color: #f7971d;
  }
`

export const Overlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  pointer-events: none;
  opacity: 0;

  &.visible {
    pointer-events: all;
    animation: fadeInBackground 0.5s forwards;
  }

  &.hidden {
    animation: fadeOutBackground 0.5s forwards;
  }

  @keyframes fadeInBackground {
    to {
      background-color: rgba(0, 0, 0, 0.6);
      opacity: 1;
    }
  }

  @keyframes fadeOutBackground {
    from {
      background-color: rgba(0, 0, 0, 0.6);
      opacity: 1;
    }
    to {
      background-color: rgba(0, 0, 0, 0);
      opacity: 0;
    }
  }
`

export const Modal = styled.div`
  background-color: ${colors.background || '#ffffff'};
  padding: 20px;
  border-radius: 8px;
  max-width: 400px;
  width: 100%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transform: translateY(-50px);
  opacity: 0;

  &.visible {
    animation: slideInModal 0.5s forwards;
  }

  &.hidden {
    animation: slideOutModal 0.5s forwards;
  }

  @keyframes slideInModal {
    to {
      transform: translateY(0);
      opacity: 1;
    }
  }

  @keyframes slideOutModal {
    from {
      transform: translateY(0);
      opacity: 1;
    }
    to {
      transform: translateY(-50px);
      opacity: 0;
    }
  }
`

export const Title = styled.h2`
  margin: 0 0 16px;
  font-size: 18px;
  text-align: center;
  color: ${colors.textPrimary};
`

export const CategoryButton = styled.button`
  width: 100%;
  padding: 10px;
  margin: 8px 0;
  border: none;
  border-radius: 4px;
  background-color: ${colors.bgPage};
  cursor: pointer;
  font-size: 16px;
  transition: 0.15s ease-in;
  color: ${colors.textPrimary};

  &:hover {
    background-color: #e0e0e0;
    color: ${colors.text};
  }
`

export const ReportIcon = styled.img`
  align-self: flex-start;
  cursor: pointer;
  width: 24px;
  height: 24px;
`

import styled from 'styled-components'
import { colors } from '@shared/consts/colors.const'
import star from '@assets/images/post/stars/star.svg'
import starHover from '@assets/images/post/stars/star-hover.svg'
import savedStar from '@assets/images/post/stars/star-pressed.svg'
import like from '@assets/images/post/likes/like.svg'
import likeHover from '@assets/images/post/likes/like-hover.svg'
import likePressed from '@assets/images/post/likes/like-pressed.svg'
import dislike from '@assets/images/post/dislikes/dislike.svg'
import dislikeHover from '@assets/images/post/dislikes/dislike-hover.svg'
import dislikePressed from '@assets/images/post/dislikes/dislike-pressed.svg'
import comment from '@assets/images/post/comments/comment.svg'
import commentHover from '@assets/images/post/comments/comment-hover.svg'
import { FONTS } from '@shared/consts/fonts.enum'

export const Container = styled.div`
  background-color: ${colors.background};

  margin-bottom: 10px;
  padding: 12px;

  border-radius: 10px;
  cursor: pointer;
`

export const HeaderImage = styled.img`
  max-height: 600px;
  height: 100%;
  width: 100%;

  object-fit: cover;
  object-position: 50% 0%;
  box-sizing: border-box;
  border-radius: 8px;
  margin-bottom: 18px;
`

export const PostHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;

  margin-bottom: 18px;
`
export const StyledUserCredentialsContainerWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
`

export const StyledUserCredentialsContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
`

export const StyledAvatar = styled.img`
  height: 42px;
  width: 42px;
  border-radius: 50%;

  object-fit: cover;
`

export const Username = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 16px;
  font-weight: 400;
  line-height: 20px;
  color: ${colors.textPrimary};
`

interface StyledStarProps {
  $isSaved?: boolean
}

export const StyledStar = styled.div<StyledStarProps>`
  height: 24px;
  width: 24px;
  transition: all 0.25s ease-in;
  background-image: ${({ $isSaved }) => ($isSaved ? `url(${savedStar})` : `url(${star})`)};
  background-size: 100% 100%;
  background-position: center;
  box-sizing: border-box;

  cursor: pointer;

  &:hover {
    background-image: ${({ $isSaved }) => ($isSaved ? `url(${savedStar})` : `url(${starHover})`)};
  }
`

export const PostTitle = styled.p`
  font-family: ${FONTS.INTER};
  font-size: 22px;
  line-height: 26px;
  font-weight: 700;
  color: ${colors.textPrimary};
  margin-bottom: 18px;
`

export const TagsContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;

  margin-bottom: 18px;
`

export const Tag = styled.p`
  font-family: ${FONTS.INTER};
  font-size: 16px;
  font-weight: 700;
  line-height: 20px;
  color: ${colors.textPrimary};
`

export const ReactionWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`

export const ReactionContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
`

export const LikesDislikesContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 10px;
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

export const StyledCount = styled.p`
  color: #fff;
  font-family: ${FONTS.INTER};
  font-size: 16px;
  line-height: 18px;
  font-weight: 400;
`

import { FC, MouseEvent, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import { ROUTES } from '@pages/router/routes.enum'
import {
  useDislikeMutation,
  useGetSavedFavoritePostsQuery,
  useLikeMutation,
  useSaveFavoritePostMutation,
} from '@api/post.api'
import { handleServerException } from '@utils/handleServerException.util'

import * as S from './post.style'

import { IPost } from '~types/post/post.type'
import { ErrorException } from '~types/error/error.type'

interface PostProps {
  currentUserId: string | undefined
  post: IPost
  userReactions?: { likedPosts: string[]; dislikedPosts: string[] } | undefined
  updateUserReactions: () => void
  updatePost: (updatedPost: IPost) => void
  refetchSavedPostsList?: () => void
  isSavedList?: boolean
}

export const Post: FC<PostProps> = ({
  post,
  userReactions,
  currentUserId,
  updateUserReactions,
  updatePost,
  isSavedList,
  refetchSavedPostsList,
}) => {
  const navigate = useNavigate()
  const [isLiked, setLiked] = useState<boolean>(false)
  const [isDisliked, setDisliked] = useState<boolean>(false)
  const [isSaved, setSaved] = useState<boolean>(false)
  const [like] = useLikeMutation()
  const [dislike] = useDislikeMutation()

  const [saveFavoritePost] = useSaveFavoritePostMutation()
  const { data: savedPostsId, refetch: updateSavedPosts } = useGetSavedFavoritePostsQuery(
    { userId: currentUserId },
    { skip: !currentUserId },
  )

  const handleLikeClick = async (e: MouseEvent<HTMLElement>) => {
    try {
      e.stopPropagation()
      const response = await like({ postId: post._id, userId: currentUserId }).unwrap()

      if (isSavedList && refetchSavedPostsList) {
        refetchSavedPostsList()
      }

      updatePost(response)
      updateUserReactions()
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    }
  }

  const handleDislikeClick = async (e: MouseEvent<HTMLElement>) => {
    try {
      e.stopPropagation()
      const response = await dislike({ postId: post._id, userId: currentUserId }).unwrap()

      if (isSavedList && refetchSavedPostsList) {
        refetchSavedPostsList()
      }

      updatePost(response)
      updateUserReactions()
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    }
  }

  const handleFavoriteStarClick = async (e: MouseEvent<HTMLElement>) => {
    try {
      e.stopPropagation()
      const response = await saveFavoritePost({ savedPostId: post._id, userId: currentUserId }).unwrap()

      updateSavedPosts()
      updatePost(response)
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    }
  }

  useEffect(() => {
    if (userReactions?.likedPosts) {
      setLiked(userReactions.likedPosts.some(like => like === post._id))
    }
    if (userReactions?.dislikedPosts) {
      setDisliked(userReactions.dislikedPosts.some(dislike => dislike === post._id))
    }
  }, [userReactions])

  useEffect(() => {
    if (savedPostsId?.savedPosts) {
      setSaved(savedPostsId.savedPosts.some(savedPost => savedPost === post._id))
    }
  }, [savedPostsId?.savedPosts])

  return (
    <S.Container onClick={() => navigate(`${ROUTES.POST_VIEW.replace(':id', post._id)}`)}>
      {post.headerImage && (
        <S.HeaderImage src={'https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/' + post.headerImage} />
      )}
      <S.PostHeader>
        <S.StyledUserCredentialsContainer>
          <S.StyledAvatar src={post.user.avatar} />
          <S.Username>{post.user.username}</S.Username>
        </S.StyledUserCredentialsContainer>
        <S.StyledStar onClick={handleFavoriteStarClick} $isSaved={isSaved} />
      </S.PostHeader>
      <S.PostTitle>{post.title}</S.PostTitle>
      <S.TagsContainer>{post.tags && post.tags.map((tag, index) => <S.Tag key={index}>#{tag}</S.Tag>)}</S.TagsContainer>
      <S.ReactionWrapper>
        <S.LikesDislikesContainer>
          <S.ReactionContainer>
            <S.Like $isLiked={isLiked} onClick={handleLikeClick} />
            <S.StyledCount>{post.likes}</S.StyledCount>
          </S.ReactionContainer>
          <S.ReactionContainer>
            <S.Dislike $isDisliked={isDisliked} onClick={handleDislikeClick} />
            <S.StyledCount>{post.dislikes}</S.StyledCount>
          </S.ReactionContainer>
        </S.LikesDislikesContainer>
        <S.ReactionContainer>
          <S.Comment />
          <S.StyledCount>{post.comments.length}</S.StyledCount>
        </S.ReactionContainer>
      </S.ReactionWrapper>
    </S.Container>
  )
}

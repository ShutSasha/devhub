import { FC } from 'react'

import * as S from './post.style'

type Comment = string

interface PostProps {
  headerImage?: string
  avatar: string
  username: string
  postTitle: string
  tags?: string[]
  likes: number
  dislikes: number
  comments: Comment[]
}

export const Post: FC<PostProps> = post => {
  return (
    <S.Container>
      {post.headerImage && <S.HeaderImage src={post.headerImage} />}
      <S.PostHeader>
        <S.StyledUserCredentialsContainer>
          <S.StyledAvatar src={post.avatar} />
          <S.Username>{post.username}</S.Username>
        </S.StyledUserCredentialsContainer>
        <S.StyledStar isSaved={false} />
      </S.PostHeader>
      <S.PostTitle>{post.postTitle}</S.PostTitle>
      <S.TagsContainer>{post.tags && post.tags.map(tag => <S.Tag>#{tag}</S.Tag>)}</S.TagsContainer>
      <S.ReactionWrapper>
        <S.LikesDislikesContainer>
          <S.ReactionContainer>
            <S.Like />
            <S.StyledCount>{post.likes}</S.StyledCount>
          </S.ReactionContainer>
          <S.ReactionContainer>
            <S.Dislike />
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

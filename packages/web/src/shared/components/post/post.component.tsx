import { FC } from 'react'

import * as S from './post.style'

import { IPost } from '~types/post/post.type'

interface PostProps {
  post: IPost
}

export const Post: FC<PostProps> = ({ post }) => {
  return (
    <S.Container>
      {post.headerImage && (
        <S.HeaderImage src={'https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/' + post.headerImage} />
      )}
      <S.PostHeader>
        <S.StyledUserCredentialsContainer>
          <S.StyledAvatar src={post.user.avatar} />
          <S.Username>{post.user.userName}</S.Username>
        </S.StyledUserCredentialsContainer>
        <S.StyledStar $isSaved={false} />
      </S.PostHeader>
      <S.PostTitle>{post.title}</S.PostTitle>
      <S.TagsContainer>{post.tags && post.tags.map((tag, index) => <S.Tag key={index}>#{tag}</S.Tag>)}</S.TagsContainer>
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

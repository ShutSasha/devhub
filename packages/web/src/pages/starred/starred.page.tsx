import { Post } from '@shared/components/post/post.component'
import { SearchInput } from '@shared/components/search-input/search-input.component'
import { MainLayout } from '@shared/layouts/main.layout'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { setPosts } from '@features/posts/posts.slice'
import { useGetUserReactionsQuery } from '@api/user.api'
import { useGetSavedFavoritePostsDetailsQuery } from '@api/post.api'

import * as _ from './starred.style'

import { IPost } from '~types/post/post.type'

export const Starred = () => {
  const userId = useAppSelector(state => state.userSlice.user?._id)
  const dispatch = useAppDispatch()
  const {
    data: userReactions,
    isLoading: userReactionsLoading,
    refetch,
  } = useGetUserReactionsQuery({ userId }, { refetchOnMountOrArgChange: true })
  const {
    data: posts,
    isLoading,
    refetch: refetchSavedPostsList,
  } = useGetSavedFavoritePostsDetailsQuery({ userId }, { refetchOnMountOrArgChange: true })

  const updatePost = (updatedPost: IPost) => {
    if (posts) {
      const updatedPosts = posts.map(post => (post._id === updatedPost._id ? { ...post, ...updatedPost } : post))
      dispatch(setPosts(updatedPosts))
    }
  }

  if (isLoading || userReactionsLoading) {
    return (
      <MainLayout>
        <div></div>
        <_.PostsContainer>
          <SearchInput placeholder="Search by post title..." />
          {isLoading && <p>Loading...</p>}
        </_.PostsContainer>
        <div></div>
      </MainLayout>
    )
  }

  return (
    <MainLayout>
      <div></div>
      <_.PostsContainer>
        {isLoading && <p>Loading...</p>}
        {Array.isArray(posts) && posts.length > 0 ? (
          posts.map(post => (
            <Post
              key={post._id}
              updatePost={updatePost}
              currentUserId={userId}
              userReactions={userReactions}
              post={post}
              updateUserReactions={refetch}
              refetchSavedPostsList={refetchSavedPostsList}
              isSavedList={true}
            />
          ))
        ) : (
          <p>No posts available.</p>
        )}
      </_.PostsContainer>
      <div></div>
    </MainLayout>
  )
}

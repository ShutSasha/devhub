import { useEffect } from 'react'
import { Post } from '@shared/components/post/post.component'
import { SearchInput } from '@shared/components/search-input/search-input.component'
import { MainLayout } from '@shared/layouts/main.layout'
import { useLazyGetPostsQuery } from '@api/post.api'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { setLoading, setPosts } from '@features/posts/posts.slice'

import { PostsContainer } from './home.style'

export const Home = () => {
  const posts = useAppSelector(state => state.postsSlice.posts)
  const isLoading = useAppSelector(state => state.postsSlice.isLoading)
  const dispatch = useAppDispatch()
  const [getPosts] = useLazyGetPostsQuery()

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const { data } = await getPosts()
        dispatch(setLoading(true))
        dispatch(setPosts(data || null))
      } catch (e) {
        console.error(e)
      } finally {
        dispatch(setLoading(false))
      }
    }
    fetchPosts()
  }, [])

  return (
    <MainLayout>
      <div>
        Lorem ipsum, dolor sit amet consectetur adipisicing elit. Libero odio est asperiores nulla non rem tempore.
        Accusamus qui voluptatem repellendus necessitatibus esse, consequatur perspiciatis voluptas totam quaerat
        veritatis dolores eveniet!
      </div>
      <PostsContainer>
        <SearchInput placeholder="Search by post title..." />
        {isLoading && <p>Loading...</p>}
        {Array.isArray(posts) && posts.length > 0 ? (
          posts.map(post => <Post key={post._id} post={post} />)
        ) : (
          <p>No posts available.</p>
        )}
      </PostsContainer>
      <div>
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Blanditiis dicta sed ullam quidem dolorem et voluptates
        itaque quaerat. Sunt deserunt asperiores nobis officiis odio suscipit cum veritatis vero officia magnam.
      </div>
    </MainLayout>
  )
}

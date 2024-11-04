import { Post } from '@shared/components/post/post.component'
import { SearchInput } from '@shared/components/search-input/search-input.component'
import { MainLayout } from '@shared/layouts/main.layout'
import { useGetPostsQuery } from '@api/post.api'

import { PostsContainer } from './home.style'

export const Home = () => {
  const { data: posts, isLoading } = useGetPostsQuery()

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

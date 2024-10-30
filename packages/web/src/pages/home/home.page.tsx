import { Post } from '@shared/components/post/post.component'
import { SearchInput } from '@shared/components/search-input/search-input.component'
import { MainLayout } from '@shared/layouts/main.layout'

import { PostsContainer } from './home.style'

export const Home = () => {
  return (
    <MainLayout>
      <div>
        Lorem ipsum, dolor sit amet consectetur adipisicing elit. Libero odio est asperiores nulla non rem tempore.
        Accusamus qui voluptatem repellendus necessitatibus esse, consequatur perspiciatis voluptas totam quaerat
        veritatis dolores eveniet!
      </div>
      <PostsContainer>
        <SearchInput placeholder="Search by post title..." />
        <Post
          headerImage="https://i.pinimg.com/564x/e9/e7/df/e9e7dfb794b7af79500a5d68b0abbb2f.jpg"
          avatar="https://i.pinimg.com/564x/7b/d1/fe/7bd1fe233cfe72fb174da94cdaf03b57.jpg"
          username="ToraDora"
          postTitle="I am cooked"
          likes={15}
          dislikes={0}
          comments={['1', '2']}
          tags={['gay', 'sex', 'IvanovYaroslav']}
        />
        <Post
          headerImage="https://i.pinimg.com/564x/68/29/44/682944a4945721736ccdfb850c9c3a2a.jpg"
          avatar="https://i.pinimg.com/564x/7b/d1/fe/7bd1fe233cfe72fb174da94cdaf03b57.jpg"
          username="ToraDora"
          postTitle="I am cooked"
          likes={15}
          dislikes={0}
          comments={['1', '2']}
          tags={['gay', 'sex', 'IvanovYaroslav']}
        />
        <Post
          avatar="https://i.pinimg.com/564x/7b/d1/fe/7bd1fe233cfe72fb174da94cdaf03b57.jpg"
          username="ToraDora"
          postTitle="I am cooked"
          likes={15}
          dislikes={0}
          comments={['1', '2']}
          tags={['gay', 'sex', 'IvanovYaroslav']}
        />
      </PostsContainer>
      <div>
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Blanditiis dicta sed ullam quidem dolorem et voluptates
        itaque quaerat. Sunt deserunt asperiores nobis officiis odio suscipit cum veritatis vero officia magnam.
      </div>
    </MainLayout>
  )
}

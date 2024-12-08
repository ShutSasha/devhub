import { useEffect, useRef, useState } from 'react'
import { Post } from '@shared/components/post/post.component'
import { SearchInput } from '@shared/components/search-input/search-input.component'
import { MainLayout } from '@shared/layouts/main.layout'
import { useLazyGetPostsQuery } from '@api/post.api'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { setPosts } from '@features/posts/posts.slice'
import { useGetUserReactionsQuery } from '@api/user.api'

import { PostsContainer } from './home.style'

import { IPost } from '~types/post/post.type'

export const Home = () => {
  const posts = useAppSelector(state => state.postsSlice.posts)
  const isLoading = useAppSelector(state => state.postsSlice.isLoading)
  const userId = useAppSelector(state => state.userSlice.user?._id)
  const dispatch = useAppDispatch()
  const [getPosts] = useLazyGetPostsQuery()
  const { data: userReactions, isLoading: userReactionsLoading, refetch } = useGetUserReactionsQuery({ userId })
  const page = useRef(1)
  const isInitialFetch = useRef(true)
  const [limit] = useState(10)
  const [fetching, setFetching] = useState<boolean>(true)

  const updatePost = (updatedPost: IPost) => {
    if (posts) {
      const updatedPosts = posts.map(post => (post._id === updatedPost._id ? { ...post, ...updatedPost } : post))
      dispatch(setPosts(updatedPosts))
    }
  }

  const scrollHandler = (e: Event) => {
    const target = e.target as Document
    const scrollTop = target.documentElement.scrollTop
    const scrollHeight = target.documentElement.scrollHeight

    if (scrollHeight - (scrollTop + window.innerHeight) < 700) {
      setFetching(true)
    }
  }

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        if (isInitialFetch.current && process.env.NODE_ENV === 'development') {
          isInitialFetch.current = false
          return
        }

        const data = await getPosts({ page: page.current, limit }).unwrap()

        if (Array.isArray(data) && page.current === 1) {
          dispatch(setPosts(data || null))
        } else if (Array.isArray(data) && page.current > 1) {
          const updatedPosts = posts ? [...posts, ...data] : data
          dispatch(setPosts(updatedPosts || null))
        }

        if (Array.isArray(data)) page.current += 1
      } catch (e) {
        console.error(e)
      } finally {
        setFetching(false)
      }
    }

    if (fetching) fetchPosts()
  }, [fetching])

  useEffect(() => {
    document.addEventListener('scroll', scrollHandler)

    return function () {
      document.removeEventListener('scroll', scrollHandler)
    }
  }, [scrollHandler])

  if (isLoading || userReactionsLoading) {
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
        </PostsContainer>
        <div>
          Lorem ipsum dolor sit amet consectetur adipisicing elit. Blanditiis dicta sed ullam quidem dolorem et
          voluptates itaque quaerat. Sunt deserunt asperiores nobis officiis odio suscipit cum veritatis vero officia
          magnam.
        </div>
      </MainLayout>
    )
  }

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
          posts.map(post => (
            <Post
              key={post._id}
              updatePost={updatePost}
              currentUserId={userId}
              userReactions={userReactions}
              post={post}
              updateUserReactions={refetch}
            />
          ))
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

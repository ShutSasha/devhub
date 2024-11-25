import { createSlice, PayloadAction } from '@reduxjs/toolkit'

import { IPost } from '~types/post/post.type'

export interface IPostsState {
  posts: IPost[] | null
  isLoading: boolean
}

const initialState: IPostsState = {
  posts: null,
  isLoading: false,
}

const postsSlice = createSlice({
  initialState,
  name: 'postsSlice',
  reducers: {
    setPosts: (state, action: PayloadAction<IPost[] | null>) => {
      state.posts = action.payload
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload
    },
  },
})

export default postsSlice

export const { setPosts, setLoading } = postsSlice.actions

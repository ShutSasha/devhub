import { IPost } from '~types/post/post.type'

export interface IUser {
  _id: string
  username: string
  name: string | null
  avatar: string
  email: string
  createdAt: string
  devPoints: number
  activationCode: string
  isActivated: false
  roles: string[]
  userRole: number[]
}

export interface UserDetailsResponse {
  _id: string
  bio: string
  avatar: string
  name: string | null
  username: string
  createdAt: string
  posts: Omit<IPost, 'comments'>[]
  comments: {
    _id: string
    postId: string
    commentText: string
    createdAt: string
  }[]
}

export interface ReqEditUserData {
  id: string
  name: string
  bio: string
}

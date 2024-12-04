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
  bio?: string | null
  userRole: string[]
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
  id: string | undefined
  name: string
  bio: string
}

export interface UserFollowersResponse {
  _id: string
  username: string
  avatar: string
}

export type UserFollowingsResponse = UserFollowersResponse[]

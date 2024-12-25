import { IComment } from '~types/comment/comment.type'
import { IUser } from '~types/user/user.type'

export interface IPost {
  _id: string
  headerImage: string
  user: IUser
  title: string
  content: string
  createdAt: string
  likes: number
  dislikes: number
  comments: IComment[]
  tags: string[]
}

export interface IReport {
  _id: string
  content: string
  sender: string
  category: string
}

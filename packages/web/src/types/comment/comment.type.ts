export interface IComment {
  _id: string
  commentText: string
  user: {
    _id: string
    username: string
    avatar: string
    devPoints: number
  }
  post: string
  createdAt: string
}

export interface CommentDto {
  content: string | null | undefined
  postId: string | undefined
  userId: string | undefined
}

export interface ICommentResponse {
  _id: string
  user: {
    _id: string
    username: string
    avatar: string
  }
  post: string
  commentText: string
  createdAt: string
}

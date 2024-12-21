import { Notification } from '@pages/notification/notification.page'

export interface GetNotificationsRes {
  read: Notification[]
  unread: Notification[]
}

type Notification = {
  id: string
  content: string
  reciever: string
  sender: {
    id: string
    username: string
    avatar: string
  }
  createdAt: string
}

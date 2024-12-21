import { MainLayout } from '@shared/layouts/main.layout'
import { ROUTES } from '@pages/router/routes.enum'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { useNavigate, useParams } from 'react-router-dom'
import { useLazyGetNotificationsByUserQuery, useReadNotificationByIdMutation } from '@api/notification.api'
import { setNotifications } from '@features/notification/notifications.slice'

import * as _ from './notification.style'

export const Notification = () => {
  const { id } = useParams()
  const notifications = useAppSelector(state => state.notificationSlice.notifications)
  const dispatch = useAppDispatch()
  const [getNotifications] = useLazyGetNotificationsByUserQuery()
  const [readNotificationById] = useReadNotificationByIdMutation()
  const navigate = useNavigate()
  const user = useAppSelector(state => state.userSlice.user)

  const handleRedirectToUserProfile = (id: string) => {
    navigate(ROUTES.USER_PROFILE.replace(':id', id))
  }

  const handleReadNotification = async () => {
    try {
      await readNotificationById({ notification_id: id }).unwrap()
      const { data } = await getNotifications({ user_id: user?._id })

      dispatch(setNotifications(data))
    } catch (e) {
      console.error(e)
    }
  }

  if (!user) {
    return null
  }

  return (
    <MainLayout>
      <div></div>
      <div>
        <_.UsersList>
          {notifications?.unread.length &&
            notifications?.unread.map(notification => (
              <_.UserListItem key={notification.id}>
                <_.UserListItemDataContainer onClick={() => handleRedirectToUserProfile(notification.sender.id)}>
                  <_.UserAvatar src={notification.sender.avatar} alt="avatar" />
                  <_.UserName>{notification.sender.username}</_.UserName>
                </_.UserListItemDataContainer>
                <_.ReadNotificationIcon onClick={handleReadNotification} />
              </_.UserListItem>
            ))}
        </_.UsersList>
      </div>
      <div></div>
    </MainLayout>
  )
}

import { createSlice, PayloadAction } from '@reduxjs/toolkit'

import { GetNotificationsRes } from '~types/notifications/notifications.types'

export interface INotificationState {
  notifications: GetNotificationsRes | undefined
}

const initialState: INotificationState = {
  notifications: undefined,
}

const notificationsSlice = createSlice({
  initialState,
  name: 'notificationSlice',
  reducers: {
    setNotifications: (state, action: PayloadAction<GetNotificationsRes | undefined>) => {
      state.notifications = action.payload
    },
  },
})

export default notificationsSlice

export const { setNotifications } = notificationsSlice.actions

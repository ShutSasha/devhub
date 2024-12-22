import { configureStore } from '@reduxjs/toolkit'
import { setupListeners } from '@reduxjs/toolkit/query'
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux'
import { api as userApi } from '@api/user.api'
import { api as authApi } from '@api/auth.api'
import { api as postApi } from '@api/post.api'
import { api as commentApi } from '@api/comment.api'
import { api as chatApi } from '@api/chat.api'
import { api as notificationApi } from '@api/notification.api'
import userSlice from '@features/user/user.slice'
import authSlice from '@features/auth/auth.slice'
import postsSlice from '@features/posts/posts.slice'
import notificationsSlice from '@features/notification/notifications.slice'


export const store = configureStore({
  reducer: {
    [userApi.reducerPath]: userApi.reducer,
    [authApi.reducerPath]: authApi.reducer,
    [postApi.reducerPath]: postApi.reducer,
    [commentApi.reducerPath]: commentApi.reducer,
    [chatApi.reducerPath]: chatApi.reducer,
    [notificationApi.reducerPath]: notificationApi.reducer,
    [notificationsSlice.name]: notificationsSlice.reducer,
    [authSlice.name]: authSlice.reducer,
    [userSlice.name]: userSlice.reducer,
    [postsSlice.name]: postsSlice.reducer,
  },
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware().concat(
      userApi.middleware,
      authApi.middleware,
      postApi.middleware,
      commentApi.middleware,
      notificationApi.middleware,
      chatApi.middleware
    ),
})

setupListeners(store.dispatch)

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
export const useAppDispatch = () => useDispatch<AppDispatch>()
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector

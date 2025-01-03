import { createBrowserRouter } from 'react-router-dom'
import { Home } from '@pages/home/home.page'
import { About } from '@pages/about/about.page'
import { SignUp } from '@pages/auth/sign-up/sign-up.page'
import { ConfirmEmail } from '@pages/auth/confirm-email/confirm-email.page'
import { Login } from '@pages/auth/login/login.page'
import { ForgotPassword } from '@pages/auth/forgot-password/forgot-password.page'
import { ForgotPasswordVerifyCode } from '@pages/auth/forgot-password/forgot-password-verify-code.page'
import { EnterNewPassword } from '@pages/auth/forgot-password/enter-new-password.page'
import { CreatePost } from '@pages/posts/create-post/create-post.page'
import { PostView } from '@pages/posts/post-view/post-view.page'
import { UserProfile } from '@pages/user/profile/profile.page'
import { UserEditProfile } from '@pages/user/edit-profile/edit-profile.page'
import { EditPost } from '@pages/posts/edit-post/edit-post.page'
import { Followers } from '@pages/friends/followers/followers.page'
import { FollowedList } from '@pages/friends/followed-list/followed-list.page'
import { Starred } from '@pages/starred/starred.page'
import { Notification } from '@pages/notification/notification.page'

import { ROUTES } from './routes.enum'
import { ChatPage } from '@pages/chat/chat.page'

const router = createBrowserRouter([
  {
    path: ROUTES.HOME,
    element: <Home />,
  },
  {
    path: ROUTES.ABOUT,
    element: <About />,
  },
  {
    path: ROUTES.SIGN_UP,
    element: <SignUp />,
  },
  {
    path: `${ROUTES.SIGN_UP}${ROUTES.CONFIRM_EMAIL}`,
    element: <ConfirmEmail />,
  },
  {
    path: `${ROUTES.LOGIN}`,
    element: <Login />,
  },
  {
    path: `${ROUTES.FORGOT_PASSWORD}`,
    element: <ForgotPassword />,
  },
  {
    path: `${ROUTES.FORGOT_PASSWORD_VERIFY}`,
    element: <ForgotPasswordVerifyCode />,
  },
  {
    path: `${ROUTES.ENTER_NEW_PASSWORD}`,
    element: <EnterNewPassword />,
  },
  {
    path: `${ROUTES.CREATE_POST}`,
    element: <CreatePost />,
  },
  {
    path: `${ROUTES.EDIT_POST}`,
    element: <EditPost />,
  },
  {
    path: `${ROUTES.POST_VIEW}`,
    element: <PostView />,
  },
  {
    path: `${ROUTES.USER_PROFILE}`,
    element: <UserProfile />,
  },
  {
    path: `${ROUTES.USER_EDIT_PROFILE}`,
    element: <UserEditProfile />,
  },
  {
    path: `${ROUTES.USER_FRIENDS_FOLLOWERS}`,
    element: <Followers />,
  },
  {
    path: `${ROUTES.USER_FRIENDS_FOLLOWED_LIST}`,
    element: <FollowedList />,
  },
  {
    path: `${ROUTES.STARRED}`,
    element: <Starred />,
  },
  {
    path: `${ROUTES.CHAT}`,
    element: <ChatPage/>
  },
  {
    path: `${ROUTES.NOTIFICATION}`,
    element: <Notification />,
  },
])

export default router

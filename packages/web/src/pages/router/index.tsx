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

import { ROUTES } from './routes.enum'

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
    path: `${ROUTES.POST_VIEW}`,
    element: <PostView />,
  },
  {
    path: `${ROUTES.USER_PROFILE}`,
    element: <UserProfile />,
  },
])

export default router

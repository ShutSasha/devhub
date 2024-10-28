import { createBrowserRouter } from 'react-router-dom'
import { Home } from '@pages/home/home.page'
import { About } from '@pages/about/about.page'
import { SignUp } from '@pages/auth/sign-up/sign-up.page'
import { ConfirmEmail } from '@pages/auth/confirm-email/confirm-email.page'
import { Login } from '@pages/auth/login/login.page'
import { ForgotPassword } from '@pages/auth/forgot-password/forgot-password.page'

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
])

export default router

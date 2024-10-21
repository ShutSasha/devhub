import { createBrowserRouter } from 'react-router-dom'
import { Home } from '@pages/home/home.page'
import { About } from '@pages/about/about.page'
import { SignUp } from '@pages/sign-up/sign-up.page'
import { ConfirmEmail } from '@pages/confirm-email/confirm-email.page'

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
])

export default router

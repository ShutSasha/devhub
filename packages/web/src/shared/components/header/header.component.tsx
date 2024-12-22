import { FC, useEffect } from 'react'
import { toast } from 'react-toastify'
import { useNavigate } from 'react-router-dom'
import logo from '@assets/images/logo.svg'
import notificationSVG from '@assets/images/header/notification.svg'
import { ROUTES } from '@pages/router/routes.enum'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { useLogoutMutation } from '@api/auth.api'
import { setNotifications } from '@features/notification/notifications.slice'
import { useGetNotificationsByUserQuery } from '@api/notification.api'
import { logout as logoutStore } from '@features/user/user.slice'

import {
  AuthContainer,
  Container,
  CreateAccountLink,
  CreatePost,
  LogInLink,
  Logo,
  Logout,
  NavList,
  UserAvatar,
  Wrapper,
  NotificationImg,
  NotificationImgContainer,
} from './header.style'
import { NavItem } from './nav-item.component'
import { navElements } from './consts/header-elements.const'

import { IUser } from '~types/user/user.type'

const AuthDisplay = () => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const user = useAppSelector(state => state.userSlice.user)
  const notifications = useAppSelector(state => state.notificationSlice.notifications)
  const { data: notification } = useGetNotificationsByUserQuery({ user_id: user?._id })
  const [logout] = useLogoutMutation()

  useEffect(() => {
    dispatch(setNotifications(notification))
  }, [notification])

  const handleCreatePostBtn = () => {
    navigate(ROUTES.CREATE_POST)
  }

  const handleLogout = async () => {
    try {
      await logout().unwrap()
      dispatch(logoutStore())
    } catch (e) {
      console.error(e)
    }
  }

  const handleClickAvatar = () => {
    if (user?._id) navigate(ROUTES.USER_PROFILE.replace(':id', user?._id))
    else {
      toast.error('Try to refresh your page or log in one more time')
    }
  }

  const handleClickNotification = () => {
    if (user?._id) navigate(ROUTES.NOTIFICATION.replace(':id', user?._id))
    else {
      toast.error('Try to refresh your page or log in one more time')
    }
  }

  return (
    <>
      {user ? (
        <>
          <CreatePost onClick={handleCreatePostBtn}>Create Post</CreatePost>
          <UserAvatar src={user.avatar} onClick={handleClickAvatar} />
          <NotificationImgContainer $count={notifications?.unread.length || 0}>
            <NotificationImg src={notificationSVG} onClick={handleClickNotification} />
          </NotificationImgContainer>
          <Logout onClick={handleLogout} />
        </>
      ) : (
        <>
          <LogInLink to={ROUTES.LOGIN}>Log in</LogInLink>
          <CreateAccountLink to={ROUTES.SIGN_UP}>Create account</CreateAccountLink>
        </>
      )}
    </>
  )
}

interface HeaderProps {
  user: IUser | null
}

export const Header: FC<HeaderProps> = ({ user }) => {
  const navigate = useNavigate()

  const handleLogoClick = () => {
    navigate(ROUTES.HOME)
  }

  return (
    <Wrapper>
      <Container>
        <Logo src={logo} onClick={handleLogoClick} />
        <NavList>
          {navElements.map(el => (
            <NavItem key={el.title} icon={el.icon} navTitle={el.title} path={el.path} user={user} />
          ))}
        </NavList>
        <AuthContainer>
          <AuthDisplay />
        </AuthContainer>
      </Container>
    </Wrapper>
  )
}

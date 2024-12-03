import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import logo from '@assets/images/logo.svg'
import { ROUTES } from '@pages/router/routes.enum'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { useLogoutMutation } from '@api/auth.api'
import { logout as logoutStore } from '@features/user/user.slice'
import { toast } from 'react-toastify'

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
} from './header.style'
import { NavItem } from './nav-item.component'
import { navElements } from './consts/header-elements.const'

const AuthDisplay = () => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const user = useAppSelector(state => state.userSlice.user)
  const [logout] = useLogoutMutation()

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

  return (
    <>
      {user ? (
        <>
          <CreatePost onClick={handleCreatePostBtn}>Create Post</CreatePost>
          <UserAvatar src={user.avatar} onClick={handleClickAvatar} />
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

export const Header = () => {
  const loadingFromState = useAppSelector(state => state.userSlice.loading)

  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsLoading(loadingFromState)
    }, 300)

    return () => clearTimeout(timer)
  }, [loadingFromState])

  const handleLogoClick = () => {
    navigate(ROUTES.HOME)
  }

  return (
    <Wrapper>
      <Container>
        <Logo src={logo} onClick={handleLogoClick} />
        <NavList>
          {navElements.map(el => (
            <NavItem key={el.title} icon={el.icon} navTitle={el.title} path={el.path} />
          ))}
        </NavList>
        <AuthContainer>{isLoading ? <div>Loading...</div> : <AuthDisplay />}</AuthContainer>
      </Container>
    </Wrapper>
  )
}

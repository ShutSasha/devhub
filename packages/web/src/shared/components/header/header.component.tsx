import logo from '@assets/images/logo.svg'
import home from '@assets/images/header/home.svg'
import friends from '@assets/images/header/friends.svg'
import star from '@assets/images/header/star.svg'
import chats from '@assets/images/header/chats.svg'
import { ROUTES } from '@pages/router/routes.enum'
import { useAppSelector } from '@app/store/store'

import { NavItem } from './nav-item.component'
import { AuthContainer, Container, CreateAccountLink, LogInLink, Logo, NavList, Wrapper } from './header.style'

type NavItem = { title: string; icon: string }

const navElements: NavItem[] = [
  { title: 'Home', icon: home },
  { title: 'Friends', icon: friends },
  { title: 'Starred', icon: star },
  { title: 'Chats', icon: chats },
]

const AuthDisplay = () => {
  const user = useAppSelector(state => state.userSlice.user)

  return (
    <>
      {user ? (
        <p>user logged in</p>
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
  const isLoading = useAppSelector(state => state.userSlice.loading) || false

  return (
    <Wrapper>
      <Container>
        <Logo src={logo} />
        <NavList>
          {navElements.map(el => (
            <NavItem key={el.title} icon={el.icon} navTitle={el.title} />
          ))}
        </NavList>
        <AuthContainer>{isLoading ? <div>Loading...</div> : <AuthDisplay />}</AuthContainer>
      </Container>
    </Wrapper>
  )
}

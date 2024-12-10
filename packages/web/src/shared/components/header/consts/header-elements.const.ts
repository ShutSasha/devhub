import home from '@assets/images/header/home.svg'
import friends from '@assets/images/header/friends.svg'
import star from '@assets/images/header/star.svg'
import chats from '@assets/images/header/chats.svg'
import { ROUTES } from '@pages/router/routes.enum'

type NavItem = { title: string; icon: string; path: string }

export const navElements: NavItem[] = [
  { title: 'Home', icon: home, path: ROUTES.HOME },
  { title: 'Friends', icon: friends, path: ROUTES.USER_FRIENDS_FOLLOWERS },
  { title: 'Starred', icon: star, path: ROUTES.STARRED },
  { title: 'Chats', icon: chats, path: ROUTES.HOME },
]

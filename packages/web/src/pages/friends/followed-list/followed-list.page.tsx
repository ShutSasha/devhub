import { useAppSelector } from '@app/store/store'
import { ROUTES } from '@pages/router/routes.enum'
import { SearchInput } from '@shared/components/search-input/search-input.component'
import { MainLayout } from '@shared/layouts/main.layout'
import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

import * as _ from '../followers/followers.style'

export const FollowedList = () => {
  const navigate = useNavigate()
  const user = useAppSelector(state => state.userSlice.user)
  const currentUrl = window.location.href

  useEffect(() => {
    if (!user) {
      navigate(ROUTES.HOME)
    }
  }, [user, navigate])

  if (!user) {
    return null
  }

  return (
    <MainLayout>
      <div></div>
      <div>
        <SearchInput placeholder="Search by users..." />
        <_.ChangeList>
          <_.ListText
            $underline={currentUrl.includes(ROUTES.USER_FRIENDS_FOLLOWERS.replace(':id', ''))}
            to={ROUTES.USER_FRIENDS_FOLLOWERS.replace(':id', user._id)}
          >
            Followers
          </_.ListText>
          <_.ListText
            $underline={currentUrl.includes(ROUTES.USER_FRIENDS_FOLLOWED_LIST.replace(':id', ''))}
            to={ROUTES.USER_FRIENDS_FOLLOWED_LIST.replace(':id', user._id)}
          >
            Followed
          </_.ListText>
        </_.ChangeList>
      </div>
      <div></div>
    </MainLayout>
  )
}

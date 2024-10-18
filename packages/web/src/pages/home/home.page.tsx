import { useGetUsersQuery } from '@api/user.api'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { setUsers } from '@features/user/user.slice'
import { useEffect } from 'react'

export const Home = () => {
  const { data } = useGetUsersQuery()
  const users = useAppSelector(state => state.user.users)
  const dispatch = useAppDispatch()

  useEffect(() => {
    if (data) dispatch(setUsers(data))
  }, [data])

  return (
    <div>
      Home
      {users && users.map(user => <p key={user.id}>{user.name}</p>)}
    </div>
  )
}

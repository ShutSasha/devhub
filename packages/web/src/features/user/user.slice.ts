import { createSlice, PayloadAction } from '@reduxjs/toolkit'

import { User } from '~types/user.type'

interface Filters {
  email?: string
  name?: string
  phone?: string
  username?: string
}

export interface UsersState {
  users: User[]
  filteredUsers: User[]
  filters: Filters
}

const initialState: UsersState = {
  users: [],
  filteredUsers: [],
  filters: {},
}

const usersSlice = createSlice({
  name: 'users',
  initialState,
  reducers: {
    setUsers: (state, action: PayloadAction<User[]>) => {
      state.users = action.payload
      state.filteredUsers = action.payload
    },
    setFilters: (state, action: PayloadAction<Filters>) => {
      state.filters = action.payload
      state.filteredUsers = state.users.filter(user => {
        return Object.entries(state.filters).every(([key, value]) =>
          user[key as keyof Filters]?.toLowerCase().includes((value as string).toLowerCase()),
        )
      })
    },
    clearFilters: state => {
      state.filters = {}
      state.filteredUsers = state.users
    },
  },
})

export const { setUsers, setFilters, clearFilters } = usersSlice.actions
export default usersSlice.reducer

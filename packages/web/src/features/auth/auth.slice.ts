import { createSlice, PayloadAction } from '@reduxjs/toolkit'

export interface IAuthState {
  username: string
  password: string
  repeatPassword: string
  email: string
}

const initialState: IAuthState = {
  username: '',
  password: '',
  repeatPassword: '',
  email: '',
}

const authSlice = createSlice({
  initialState,
  name: 'authSlice',
  reducers: {
    setUsername: (state, action: PayloadAction<string>) => {
      state.username = action.payload
    },
    setPassword: (state, action: PayloadAction<string>) => {
      state.password = action.payload
    },
    setRepeatPassword: (state, action: PayloadAction<string>) => {
      state.repeatPassword = action.payload
    },
    setEmail: (state, action: PayloadAction<string>) => {
      state.email = action.payload
    },
  },
  selectors: {
    getUsername: state => state.username,
    getPassword: state => state.password,
    getRepeatPassword: state => state.repeatPassword,
    getEmail: state => state.email,
  },
})

export default authSlice

export const { setUsername, setPassword, setRepeatPassword, setEmail } = authSlice.actions
export const { getUsername, getPassword, getRepeatPassword, getEmail } = authSlice.selectors

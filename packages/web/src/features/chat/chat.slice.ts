import { createSlice, PayloadAction } from '@reduxjs/toolkit'

export interface IChatState {
  activeChatId: string | undefined
}

const initialState: IChatState = {
  activeChatId: undefined,
}

const chatsSlice = createSlice({
  initialState,
  name: 'chatSlice',
  reducers: {
    setActiveChatId: (state, action: PayloadAction<string | undefined>) => {
      state.activeChatId = action.payload
    },
  },
})

export default chatsSlice

export const { setActiveChatId } = chatsSlice.actions

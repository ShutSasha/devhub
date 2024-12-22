import styled from 'styled-components'
import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'

export const ChatContainer = styled.div`
  margin-bottom: 10px;
  padding: 12px;
`
export const SideBarHeader = styled.div`
  background-color: ${colors.background};
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
`

export const MessageInputContainer = styled.div`
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom-left-radius: 5px;
  border-bottom-right-radius: 5px;
  background-color: ${colors.background};
`

export const MessageInput = styled.input`
  background-color: ${colors.background};
  flex: 1;
  width: 100%;
  padding: 10px 0;
  border-radius: 5px;
  font-size: 16px;
  font-family: ${FONTS.MONTSERRAT};
  color: #fff;
`

export const SendButton = styled.button`
  padding: 10px;
  margin-left: 10px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
`

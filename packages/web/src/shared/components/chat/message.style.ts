import styled from 'styled-components'
import { FONTS } from '@shared/consts/fonts.enum'
import { colors } from '@shared/consts/colors.const'

export const MessagesContainer = styled.div`
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow-y: auto;
    min-height: calc(100vh - 300px);
    max-height: calc(100vh - 300px);

  background-color: transparent;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: ${colors.background};
    border-radius: 15px;
  }

  &::-webkit-scrollbar-thumb:hover {
    background-color: ${colors.accent};
  }

  &::-webkit-scrollbar-track {
    background-color: transparent;
  }
`

export const MessageBubble = styled.div<{ $isOwnMessage: boolean }>`
  max-width: 60%;
  padding: 10px 15px;
  border-radius: ${({ $isOwnMessage }) => ($isOwnMessage ? '10px 10px 3px 10px;' : '10px 10px 10px 3px;')};
  background-color: #2a2a2d;
  color: ${({ $isOwnMessage }) => ($isOwnMessage ? '#fff' : '#000')};
  align-self: ${({ $isOwnMessage }) => ($isOwnMessage ? 'flex-end' : 'flex-start')};
  word-wrap: break-word;
  gap: 5px;
`

export const MessageContent = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
`

export const MessageText = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  color: #fff;
`

export const DateContainer = styled.span`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 10px;
  color: #bbb;
  margin-left: 10px;
  align-self: flex-end;
`

import styled from 'styled-components'
import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'

export  const MessagesContainer = styled.div`
    flex: 1;
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 10px;
    overflow-y: auto; 
    max-height: calc(100vh - 300px);
    background-color: transparent;
    scrollbar-width: thin; 
    scrollbar-color: #ccc transparent;
    
    &::-webkit-scrollbar {
        width: 8px;
    }

    &::-webkit-scrollbar-track {
        background: transparent;
    }

    &::-webkit-scrollbar-thumb {
        background: #ccc;
        border-radius: 4px;
    }

    &::-webkit-scrollbar-thumb:hover {
        background: #999;
    }
`;

export  const MessageBubble = styled.div<{ isOwnMessage: boolean }>`
  max-width: 60%;
  padding: 10px 15px;
  border-radius: ${({ isOwnMessage }) => (isOwnMessage ? '10px 10px 3px 10px;' : '10px 10px 10px 3px;')};
  background-color: #2A2A2D;
  color: ${({ isOwnMessage }) => (isOwnMessage ? '#fff' : '#000')};
  align-self: ${({ isOwnMessage }) => (isOwnMessage ? 'flex-end' : 'flex-start')};
  word-wrap: break-word;
    gap: 5px
`;

export const MessageContent = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
`;

export const MessageText = styled.p`
    font-family: ${FONTS.MONTSERRAT};
    color: #fff
`

export const DateContainer = styled.span`
    font-family: ${FONTS.MONTSERRAT};
    font-size: 10px;
    color: #bbb;
    margin-left: 10px;
    align-self: flex-end;
`;
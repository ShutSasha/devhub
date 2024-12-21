import React, { FC } from 'react'
import { StyledAvatar, Username } from '@shared/components/post/post.style'
import {
  ChatGrayLine,
  ChatPreviewContainer,
  ChatPreviewDetails,
  ChatPreviewLastMessage,
  ChatPreviewUsername,
} from '@pages/chat/components/chat-preview.style'
import { GrayLine } from '@pages/posts/post-view/post-view.style'
import { IChatPreview } from '~types/chat/chat.type'

interface ChatPreviewProps {
  username: string
  avatar: string
  lastmessage: string | null
  isChatActive: boolean | undefined
  onClick: () => void
}

export const ChatPreview: FC<ChatPreviewProps> = ({ username, avatar, lastmessage, isChatActive, onClick }) => {
  return (
    <>
      <ChatGrayLine />
      <ChatPreviewContainer onClick={onClick} isChatActive={isChatActive}>
        <StyledAvatar src={avatar} />
        <ChatPreviewDetails>
          <ChatPreviewUsername>{username}</ChatPreviewUsername>
          <ChatPreviewLastMessage>{lastmessage != null ? lastmessage : 'No one message'}</ChatPreviewLastMessage>
        </ChatPreviewDetails>
      </ChatPreviewContainer>
    </>
  )
}

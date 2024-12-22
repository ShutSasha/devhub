import { FC } from 'react'
import { StyledAvatar } from '@shared/components/post/post.style'
import {
  ChatPreviewContainer,
  ChatPreviewDetails,
  ChatPreviewLastMessage,
  ChatPreviewUsername,
} from '@pages/chat/components/chat-preview.style'

interface ChatPreviewProps {
  username: string
  avatar: string
  lastmessage: string | null
  isChatActive: boolean | undefined
  onClick: () => void
}

export const ChatPreview: FC<ChatPreviewProps> = ({ username, avatar, lastmessage, isChatActive, onClick }) => {
  return (
    <ChatPreviewContainer onClick={onClick} $isChatActive={isChatActive}>
      <StyledAvatar src={avatar} />
      <ChatPreviewDetails>
        <ChatPreviewUsername>{username}</ChatPreviewUsername>
        <ChatPreviewLastMessage>{lastmessage != null ? lastmessage : 'No one message'}</ChatPreviewLastMessage>
      </ChatPreviewDetails>
    </ChatPreviewContainer>
  )
}

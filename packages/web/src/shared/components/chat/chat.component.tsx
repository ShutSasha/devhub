import { useNavigate } from 'react-router-dom'
import React, { FC, useEffect, useRef, useState } from 'react'
import {
  ChatContainer,
  SideBarHeader,
  MessageInput,
  SendButton,
  MessageInputContainer,
} from '@shared/components/chat/chat.style'
import { StyledAvatar, StyledUserCredentialsContainer, Username } from '@shared/components/post/post.style'
import { MessagesContainer } from '@shared/components/chat/message.style'
import { Message } from '@shared/components/chat/message.component'
import { ROUTES } from '@pages/router/routes.enum'
import sendSVG from '@assets/images/chat/send.svg'

import { IMessage } from '~types/chat/chat.type'

interface IChatProps {
  messages: IMessage[]
  username: string
  avatarUrl: string
  chatId: string
  userId: string
  userReceiverId?: string
  sendMessage: (messageInput: string) => void
}

export const Chat: FC<IChatProps> = ({ username, avatarUrl, messages, userId, userReceiverId, sendMessage }) => {
  const navigate = useNavigate()
  const [messageInput, setMessageInput] = useState<string>('')
  const chatContainerRef = useRef<HTMLDivElement | null>(null)

  const onSendMessage = () => {
    sendMessage(messageInput)
    setMessageInput('')
  }

  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight
    }
  }, [messages])

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      onSendMessage()
    }
  }

  const handleRedirectToUserProfile = (id: string | undefined) => {
    if (id) {
      navigate(ROUTES.USER_PROFILE.replace(':id', id))
    }
  }

  return (
    <ChatContainer>
      <SideBarHeader>
        <StyledUserCredentialsContainer>
          <StyledAvatar onClick={() => handleRedirectToUserProfile(userReceiverId)} src={avatarUrl} />
          <Username>{username}</Username>
        </StyledUserCredentialsContainer>
      </SideBarHeader>

      <MessagesContainer ref={chatContainerRef}>
        {messages.length > 0 ? messages.map((message, index) => (
          <Message
            key={index}
            text={message.content}
            isOwnMessage={message.userSender === userId}
            createdAt={message.createdAt}
          />
        )) : (
          <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', flex: '1' }}>No messages</div>
        )}
      </MessagesContainer>

      <MessageInputContainer>
        <MessageInput
          placeholder="Write a message..."
          value={messageInput}
          onChange={e => setMessageInput(e.target.value)}
          onKeyDown={handleKeyDown}
        />
        <SendButton onClick={onSendMessage}>
          <img src={sendSVG} alt="send button" />
        </SendButton>
      </MessageInputContainer>
    </ChatContainer>
  )
}

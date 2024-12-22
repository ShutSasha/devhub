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

import { IMessage, MainChatResponse } from '~types/chat/chat.type'
import { ROUTES } from '@pages/router/routes.enum'
import { IndentStyle } from 'typescript'

interface IChatProps {
  messages: IMessage[]
  username: string
  avatarUrl: string
  chatId: string
  userId: string
  userReceiverId?: string
  sendMessage: (messageInput: string) => void
}

export const Chat: FC<IChatProps> = ({
  username,
  avatarUrl,
  messages,
  chatId,
  userId,
  userReceiverId,
  sendMessage,
}) => {
  const navigate = useNavigate()
  const [messageInput, setMessageInput] = useState<string>('')
  const messagesEndRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView()
  }, [messages])
  const onSendMessage = () => {
    sendMessage(messageInput)
    setMessageInput('')
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      onSendMessage()
    }
  }

  const handleRedirectToUserProfile = (id: string | undefin) => {
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

      <MessagesContainer>
        {messages.map(message => (
          <Message
            key={message._id}
            text={message.content}
            isOwnMessage={message.userSender === userId}
            createdAt={message.createdAt}
          />
        ))}
      </MessagesContainer>

      <MessageInputContainer>
        <MessageInput
          placeholder="Write a message..."
          value={messageInput}
          onChange={e => setMessageInput(e.target.value)}
          onKeyDown={handleKeyDown}
        />
        <SendButton onClick={onSendMessage}>
          <img src="/src/assets/images/chat/send.svg" alt="send button" />
        </SendButton>
      </MessageInputContainer>
    </ChatContainer>
  )
}

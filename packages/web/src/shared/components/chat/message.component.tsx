import React from 'react'
import { DateContainer, MessageBubble, MessageContent, MessageText } from '@shared/components/chat/message.style'
import { parseDate } from '@utils/parseDate.util'

export const Message = ({
  text,
  isOwnMessage,
  createdAt,
}: {
  text: string
  isOwnMessage: boolean
  createdAt: string
}) => {
  return (
    <MessageBubble $isOwnMessage={isOwnMessage}>
      <MessageContent>
        <MessageText>{text}</MessageText>
        <DateContainer>{parseDate(createdAt)}</DateContainer>
      </MessageContent>
    </MessageBubble>
  )
}

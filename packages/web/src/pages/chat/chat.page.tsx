import { useEffect, useState } from 'react'
import { ChatLayout } from '@shared/layouts/chat/chat.layout'
import { ChatListContainer } from '@pages/chat/chat.style'
import { Chat } from '@shared/components/chat/chat.component'
import { ChatPreview } from '@pages/chat/components/chat-preview.component'
import { useGetChatsByUserQuery, useGetChatByIdQuery, useGetFirstChatQuery } from '@api/chat.api'
import { useParams } from 'react-router-dom'
import { HubConnection, HubConnectionBuilder } from '@microsoft/signalr'
import { skipToken } from '@reduxjs/toolkit/query'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { setActiveChatId } from '@features/chat/chat.slice'

import { IMessage } from '~types/chat/chat.type'

export const ChatPage = () => {
  const { id } = useParams()
  const { data: userChats, isLoading, refetch: refetchPreviews } = useGetChatsByUserQuery({ userId: id })
  const { data: lastChat } = useGetFirstChatQuery({ userId: id })
  const [connection, setConnection] = useState<HubConnection | null>(null)
  const [messages, setMessages] = useState<IMessage[]>([])
  const dispatch = useAppDispatch()
  const activeChatId = useAppSelector(state => state.chatSlice.activeChatId)

  const { data: activeChat, refetch: refetchActiveChat } = useGetChatByIdQuery(
    activeChatId
      ? {
          chatId: activeChatId,
          userId: id,
        }
      : skipToken,
  )

  useEffect(() => {
    if (!activeChatId && lastChat?.chatId) {
      dispatch(setActiveChatId(lastChat.chatId))
    }
  }, [lastChat])

  useEffect(() => {
    refetchPreviews()
  }, [activeChatId])

  useEffect(() => {
    connection?.stop()

    const newConnection = new HubConnectionBuilder()
      .withUrl('http://localhost:5231/chat')
      .withAutomaticReconnect()
      .build()

    setConnection(prev => {
      if (prev) {
        prev.stop()
      }
      return newConnection
    })

    return () => {
      newConnection.stop()
    }
  }, [activeChatId])

  useEffect(() => {
    if (activeChat) {
      setMessages(activeChat.chatMessages || [])
    } else if (lastChat) {
      setMessages(lastChat.chatMessages || [])
    } else {
      setMessages([])
    }
  }, [activeChat, lastChat])

  const handleChatSelect = async (chatId: string) => {
    if (connection) {
      await connection.stop()
    }

    dispatch(setActiveChatId(chatId))
    refetchActiveChat()
  }

  useEffect(() => {
    const initiateConnection = async () => {
      if (connection) {
        try {
          await connection.stop()
          await connection.start()

          const participantId = activeChat?.participantDetails.id || lastChat?.participantDetails.id
          if (id && participantId) {
            await connection.invoke('JoinChat', id, participantId)
          }

          connection.off('ReceiveMessage')
          connection.on('ReceiveMessage', (message: IMessage) => {
            setMessages(prevMessages => [...prevMessages, message])
          })

          await refetchPreviews()
        } catch (error) {
          console.error('Error starting connection or joining chat: ', error)
        }
      }
    }

    initiateConnection()

    return () => {
      connection?.off('ReceiveMessage')
    }
  }, [connection, lastChat, activeChat])

  const sendMessage = async (messageInput: string) => {
    if (connection && activeChatId && messageInput.trim()) {
      try {
        await connection.invoke('SendMessage', activeChatId, id, messageInput)
      } catch (error) {
        console.error('Error sending message: ', error)
      }
    } else if (!activeChatId && connection && messageInput.trim() && lastChat) {
      try {
        await connection.invoke('SendMessage', lastChat.chatId, id, messageInput)
      } catch (error) {
        console.error('Error sending message: ', error)
      }
    }
  }

  return (
    <ChatLayout>
      <ChatListContainer>
        {!isLoading && Array.isArray(userChats) ? (
          userChats.map(userChat => (
            <ChatPreview
              key={userChat.chatId}
              isChatActive={activeChatId === userChat.chatId}
              username={userChat.participants.username}
              avatar={userChat.participants.avatarUrl}
              lastmessage={userChat.lastMessage}
              onClick={() => handleChatSelect(userChat.chatId)}
            />
          ))
        ) : (
          <p>Loading chats...</p>
        )}
      </ChatListContainer>
      {activeChatId && id && activeChat ? (
        <Chat
          sendMessage={sendMessage}
          chatId={activeChat.chatId}
          userId={id}
          userReceiverId={activeChat.participantDetails.id}
          username={activeChat.participantDetails.username || 'Unknown User'}
          avatarUrl={activeChat.participantDetails.avatarUrl || '/default-avatar.png'}
          messages={messages}
        />
      ) : lastChat && id ? (
        <Chat
          sendMessage={sendMessage}
          chatId={lastChat.chatId}
          userId={id}
          userReceiverId={lastChat.participantDetails.id}
          username={lastChat.participantDetails.username || 'Unknown User'}
          avatarUrl={lastChat.participantDetails.avatarUrl || '/default-avatar.png'}
          messages={messages}
        />
      ) : (
        <p>No active chat selected or available</p>
      )}
    </ChatLayout>
  )
}

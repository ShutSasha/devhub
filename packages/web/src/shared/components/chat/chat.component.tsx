import {
  SideBarHeader,
  ChatContainer,
  MessageInput,
  SendButton, MessageInputContainer,
} from '@shared/components/chat/chat.style'
import { StyledAvatar, StyledUserCredentialsContainer, Username } from '@shared/components/post/post.style'
import { MessagesContainer } from '@shared/components/chat/message.style'
import { Message } from '@shared/components/chat/message.component'
import {useEffect, useState} from 'react'
import {HubConnection, HubConnectionBuilder} from '@microsoft/signalr';

export const Chat = () => {
  const [connection, setConnection] = useState<HubConnection | null>(null);
  const [messages, setMessages] = useState<string[]>([]);

  useEffect(() => {
    const newConnection = new HubConnectionBuilder()
        .withUrl('http://localhost:5230/chat')
        .withAutomaticReconnect()
        .build();

    setConnection(newConnection);
  }, []);

  useEffect(() => {
    if (connection) {
      connection.start()
          .then(() => {
            console.log('Connected!');
            connection.on('ReceiveMessage', (message: string) => {
              setMessages(prevMessages => [...prevMessages, message]);
            });
          })
          .catch(error => console.log('Connection failed: ', error));
    }
  }, [connection]);

  const sendMessage = async () => {
    if (connection) {
      try {
        await connection.invoke('SendMessage', 'chatId', 'userId', 'Hello!');
      } catch (error) {
        console.error('Error sending message: ', error);
      }
    }
  };

  return (
    <>
      <ChatContainer>
        <SideBarHeader>
          <StyledUserCredentialsContainer>
            <StyledAvatar key={'da'}
                          src={'https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/user_icons/6728ea8d96458776cf19b7db/00:00:27ruby-oshinoko.png'} />
            <Username>Test</Username>
          </StyledUserCredentialsContainer>
        </SideBarHeader>

        <MessagesContainer>
          <Message text="Hello, how are you?" isOwnMessage={false} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={false} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
          <Message text="Hello, how are you?" isOwnMessage={true} createdAt={'2024-12-09T17:00:51.870+00:00'} />
        </MessagesContainer>

        <MessageInputContainer>
          <MessageInput placeholder="Write a message..." />
          <SendButton>
            <img src="/src/assets/images/chat/send.svg" alt="send button"/>
          </SendButton>
        </MessageInputContainer>

      </ChatContainer>
    </>
  )
}
import React, {useEffect} from 'react'
import {ChatLayout} from '@shared/layouts/chat/chat.layout'
import {ChatListContainer} from '@pages/chat/chat.style'
import {SearchInput} from '@shared/components/search-input/search-input.component'
import {Chat} from '@shared/components/chat/chat.component'
import {ChatPreview} from '@pages/chat/components/chat-preview.component'
import {useGetChatsByUserQuery} from '@api/chat.api'
import {useParams} from 'react-router-dom'

export const ChatPage = () => {
    const {id} = useParams()
    const {data: userChats, isLoading} = useGetChatsByUserQuery({userId: id})

    useEffect(() => {
        console.log(userChats)
    }, [userChats])

    return (
        <ChatLayout>
            <ChatListContainer>
                <SearchInput isChatSearch={true} placeholder="Search by chats..."/>
                {!isLoading && Array.isArray(userChats) ? (
                    userChats.map(userChat => (
                        <ChatPreview
                            key={userChat.chatId}
                            username={userChat.participants.username}
                            avatar={userChat.participants.avatarUrl}
                            lastmessage={userChat.lastMessage}
                        />
                    ))
                ) : (
                    <p>Loading chats...</p>
                )}
            </ChatListContainer>
            <Chat/>
        </ChatLayout>
    )
}

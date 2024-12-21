import React, { FC } from 'react'
import { StyledAvatar, Username } from '@shared/components/post/post.style'
import {
  ChatPreviewContainer, ChatPreviewDetails,
  ChatPreviewLastMessage,
  ChatPreviewUsername,
} from '@pages/chat/components/chat-preview.style'
import { GrayLine } from '@pages/posts/post-view/post-view.style'
import { IChatPreview } from '~types/chat/chat.type'

interface ChatProps {
  chatPreviews: IChatPreview
}

//export const ChatPreview: FC<ChatProps> = ({ chatPreviews }) => {
//   return (
//     <>
//       <GrayLine />
//       <ChatPreviewContainer>
//         <StyledAvatar src={chatPreviews.participant.avatarUrl} />
//         <ChatPreviewDetails>
//           <ChatPreviewUsername>{chatPreviews.participant.username}</ChatPreviewUsername>
//           <ChatPreviewLastMessage>{chatPreviews.lastMessage}</ChatPreviewLastMessage>
//         </ChatPreviewDetails>
//       </ChatPreviewContainer>
//     </>
//   )
// }

interface ChatPreviewProps {
    username: string;
    avatar: string;
    lastmessage: string | null;
}
export const ChatPreview: FC<ChatPreviewProps> = ({ username,avatar,lastmessage}) => {
    return (
        <>
            <GrayLine />
            <ChatPreviewContainer>
                <StyledAvatar src={avatar} />
                <ChatPreviewDetails>
                    <ChatPreviewUsername>{username}</ChatPreviewUsername>
                    <ChatPreviewLastMessage>{lastmessage != null ? lastmessage : "No one message"}</ChatPreviewLastMessage>
                </ChatPreviewDetails>
            </ChatPreviewContainer>
        </>
    )
}


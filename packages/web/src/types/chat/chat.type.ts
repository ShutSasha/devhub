export type ChatResponse = IChatPreview[];


export interface IChatPreview {
    chatId: string;
    lastMessage: string | null;
    timestamp: string;
    participants: {
        id: string | null;
        username: string;
        avatarUrl: string;
    }
}

export interface MainChatResponse {
    chatId: string;
    participantDetails: {
        id: string;
        username: string;
        avatarUrl: string;
    }
    chatMessages: IMessage[]
}

export interface IMessage {
    _id: string;
    chat: string;
    userSender: string;
    content: string;
    createdAt: string
}
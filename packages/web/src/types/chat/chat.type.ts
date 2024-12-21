export type ChatResponse = IChatPreview[];

export interface IChatProps{
  chatPreviews : IChatPreview[]
}

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
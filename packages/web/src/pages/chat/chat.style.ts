import styled from 'styled-components'

export const ChatListContainer = styled.div`
  display: flex;
  flex-direction: column;
  padding: 12px;
  flex: 1;
  overflow-y: auto;
  max-height: calc(100vh - 100px);
  scrollbar-width: thin;
  scrollbar-color: #ccc transparent;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: #ccc;
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb:hover {
    background: #999;
  }
`

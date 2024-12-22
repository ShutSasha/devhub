import styled from 'styled-components'
import { FONTS } from '@shared/consts/fonts.enum'
import { colors } from '@shared/consts/colors.const'

export const ChatPreviewContainer = styled.div<{ $isChatActive: boolean | undefined }>`
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px;
  background-color: ${({ $isChatActive }) => ($isChatActive ? 'rgba(247, 151, 29, 0.42)' : 'transparent')};
  border-radius: 2px;
  transition: background-color 0.3s ease;

  &:hover {
    background-color: rgba(247, 151, 29, 0.42);
  }
`
export const ChatGrayLine = styled.hr`
  height: 1px;
  width: 100%;
  background-color: #ffffff66;
  border: none;
`

export const ChatPreviewDetails = styled.div`
  display: flex;
  flex-direction: column;
  margin-left: 10px;
`

export const ChatPreviewUsername = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 16px;
  font-weight: 600;
  line-height: 20px;
  color: ${colors.textPrimary};
`

export const ChatPreviewLastMessage = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 14px;
  font-weight: 400;
  line-height: 18px;
  color: ${colors.textSecondary};
  margin-top: 5px;
`

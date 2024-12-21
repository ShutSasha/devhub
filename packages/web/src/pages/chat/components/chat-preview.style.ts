import styled from 'styled-components';
import { FONTS } from '@shared/consts/fonts.enum';
import { colors } from '@shared/consts/colors.const';

export const ChatPreviewContainer = styled.div`
  display: flex;
  align-items: center;
  padding: 5px;
  background-color: transparent;
`;

export const ChatPreviewDetails = styled.div`
  display: flex;
  flex-direction: column;
  margin-left: 10px;
`;

export const ChatPreviewUsername = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 16px;
  font-weight: 600;
  line-height: 20px;
  color: ${colors.textPrimary};
`;

export const ChatPreviewLastMessage = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 14px;
  font-weight: 400;
  line-height: 18px;
  color: ${colors.textSecondary};
  margin-top: 5px;
`;

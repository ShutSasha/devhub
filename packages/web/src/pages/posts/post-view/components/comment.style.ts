import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'

export const CommentContainer = styled.div`
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.4);
`

export const CommentInnerContainer = styled.div`
  display: flex;
  flex-direction: column;
`

export const CommentHeaderContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 4px;
`

export const Username = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 500;
  font-size: 20px;
  color: ${colors.textPrimary};
`

export const CommentPostedDate = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 400;
  font-size: 14px;
  color: rgba(237, 237, 237, 0.61);
`

export const CommentText = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 400;
  font-size: 18px;
  color: ${colors.textPrimary};
`

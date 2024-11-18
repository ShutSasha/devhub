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
  width: 100%;
  display: flex;
  flex-direction: column;
`

export const CommentHeaderContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18px;
  margin-bottom: 4px;
`

export const CommentHeaderInnerContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 4px;
`

export const RemoveBtn = styled.button`
  font-family: ${FONTS.MONTSERRAT};
  background-color: transparent;
  font-size: 16px;
  line-height: 20px;
  padding: 4px 8px;
  border-radius: 5px;
  align-self: flex-start;
  cursor: pointer;
  transition: all 0.25s ease-in;
  box-sizing: border-box;
  color: #f7971d;
  border: 1px solid #f7971d;

  &:hover {
    background-color: #f7971d;

    border: 1px solid transparent;
    color: #fff;
  }
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

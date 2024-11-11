import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'

export const PostTitle = styled.p`
  font-size: 20px;
  line-height: 24px;
  font-familt: ${FONTS.INTER};
  font-weight: 700;
  margin-bottom: 18px;
`

export const PostImage = styled.img`
  max-height: 500px;
  height: 100%;
  width: 100%;

  object-fit: cover;
  object-position: 50% 0%;
  box-sizing: border-box;
  border-radius: 8px;
  margin-bottom: 18px;
`

export const PostCreationData = styled.p`
  font-size: 16px;
  line-height: 20px;
  font-familt: ${FONTS.INTER};
  color: #ededed7d;
  font-weight: 700;
  margin-bottom: 18px;
`

export const PostTagsContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
`

export const PostTag = styled.p`
  font-size: 16px;
  line-height: 20px;
  font-familt: ${FONTS.INTER};
  font-weight: 700;
`

export const ContentText = styled.p`
  font-size: 16px;
  line-height: 24px;
  font-familt: ${FONTS.INTER};
  color: #fff;
  font-weight: 500;
  text-justify: inter-word;
  text-align: justify;
`

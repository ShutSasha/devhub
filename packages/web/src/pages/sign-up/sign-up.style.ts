import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'

export const StyledText = styled.h1`
  font-size: 32px;
  line-height: 48px;
  font-weight: 600;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  display: flex;
  margin-bottom: 16px;
`

export const BlackText = styled.span`
  font-family: ${FONTS.MONTSERRAT};
  color: #000;
`

export const OrangeText = styled.span`
  font-family: ${FONTS.MONTSERRAT};
  color: #f7971d;
`

export const InputsContainer = styled.div`
  display: flex;
  gap: 16px;
  flex-direction: column;
  width: 100%;
  marginbottom: 5px;
`

export const SixDigitalCodeSpan = styled.span`
  display: inline-block;
  font-family: ${FONTS.INTER};
  font-size: 14px;
  line-height: 21px;
  color: rgba(13, 26, 38, 0.5);
  margin-bottom: 16px;
`

import styled from 'styled-components'
import { FONTS } from '@shared/consts/fonts.enum'

export const StyledAuthLayout = styled.div`
  display: grid;
  grid-template-columns: minmax(400px, 1fr) 2fr;
  height: 100vh;
`

export const StyledAuthForm = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 0 123.5px;

  @media (max-width: 1740px) {
    padding: 0 80px;
  }
`

export const FeedBack = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to bottom, #161515 0%, #f7971d 85%);
  padding: 0 10px;
`

export const Title = styled.h2`
  font-family: ${FONTS.MANROPE};
  font-weight: 600;
  font-size: 40px;
  line-height: 60px;
  color: #e9f9fc;
  text-align: center;
  margin-bottom: 48px;
`

export const FeedBackImg = styled.img`
  width: 96px;
  height: 96px;
  object-fit: cover;
  margin-bottom: 24px;
`

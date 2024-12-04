import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'
import { Link } from 'react-router-dom'

export const ChangeList = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-bottom: 16px;
`

export const ListText = styled(Link)<{ $underline: boolean }>`
  font-family: ${FONTS.MONTSERRAT};
  font-weight: 500;
  line-height: 20px;
  font-size: 16px;
  color: #fff;
  text-decoration: ${({ $underline }) => ($underline ? 'underline' : 'none')};
`

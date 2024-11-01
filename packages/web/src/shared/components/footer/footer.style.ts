import styled from 'styled-components'
import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'

export const Wrapper = styled.div`
  background-color: ${colors.background};
  margin-top: auto;
`

export const Container = styled.div`
  max-width: 1440px;
  width: 100%;
  padding: 20px 0;
  margin: 0 auto;
`

export const EmphasizeLine = styled.hr`
  background: #f7971d;
  height: 1px;
  border: none;
  margin-bottom: 16px;
`

export const InfoListContainer = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
`

export const InfoColumn = styled.div`
  display: flex;
  flex-direction: column;
  justifu-content: start;

  margin-bottom: 16px;
`

export const ColumnTitle = styled.p`
  font-family: ${FONTS.INTER};
  font-size: 16px;
  line-height: 24px;
  font-weight: 600;
  color: ${colors.textPrimary};
`

export const ColumnText = styled(ColumnTitle)`
  font-size: 14px;
  font-weight: 400;
`

export const FooterLogoContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`

export const Logo = styled.img`
  height: 38px;
`

export const AllRightsText = styled.p`
  font-family: ${FONTS.INTER};
  font-size: 14px;
  line-height: 24px;
  color: #f7971d;
`

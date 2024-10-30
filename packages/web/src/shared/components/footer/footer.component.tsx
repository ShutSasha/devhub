import logo from '@assets/images/logo.svg'

import * as S from './footer.style'

const products: string[] = ['Libraries', 'CLI', 'Studio', 'Hosting']
const resources: string[] = ['Docs', 'Learn', 'Examples', 'Changelog']
const companyInfo: string[] = ['About us', 'Contact', 'Blog']

export const Footer = () => {
  return (
    <S.Wrapper>
      <S.Container>
        <S.EmphasizeLine />
        <S.InfoListContainer>
          <S.InfoColumn>
            <S.ColumnTitle>Products</S.ColumnTitle>
            {products.map(product => (
              <S.ColumnText>{product}</S.ColumnText>
            ))}
          </S.InfoColumn>
          <S.InfoColumn>
            <S.ColumnTitle>Resources</S.ColumnTitle>
            {resources.map(product => (
              <S.ColumnText>{product}</S.ColumnText>
            ))}
          </S.InfoColumn>
          <S.InfoColumn>
            <S.ColumnTitle>Company</S.ColumnTitle>
            {companyInfo.map(product => (
              <S.ColumnText>{product}</S.ColumnText>
            ))}
          </S.InfoColumn>
        </S.InfoListContainer>
        <S.EmphasizeLine />
        <S.FooterLogoContainer>
          <S.Logo src={logo} />
          <S.AllRightsText>© 2024 DevHub. All rights reserved.</S.AllRightsText>
        </S.FooterLogoContainer>
      </S.Container>
    </S.Wrapper>
  )
}

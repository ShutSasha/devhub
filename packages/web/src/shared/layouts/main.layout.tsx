import { FC, ReactNode } from 'react'
import styled from 'styled-components'
import { Header } from '@shared/components/header/header.component'
import { colors } from '@shared/consts/colors.const'
import { Footer } from '@shared/components/footer/footer.component'

const Wrapper = styled.div`
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  color: ${colors.textPrimary};
  background-color: ${colors.bgPage};
`

const Container = styled.div`
  max-width: 1440px;
  width: 100%;
  margin: 0 auto;
  padding: 0 10px;

  display: grid;
  grid-template-columns: 300px minmax(300px, 1fr) 300px;
  gap: 36px;
`

interface MainLayoutProps {
  children: ReactNode
}

export const MainLayout: FC<MainLayoutProps> = ({ children }) => {
  return (
    <Wrapper>
      <Header />
      <Container>{children}</Container>
      <Footer />
    </Wrapper>
  )
}

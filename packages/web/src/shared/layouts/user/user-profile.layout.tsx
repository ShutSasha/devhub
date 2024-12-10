import { FC, ReactNode } from 'react'
import styled from 'styled-components'
import { Header } from '@shared/components/header/header.component'
import { colors } from '@shared/consts/colors.const'
import { Footer } from '@shared/components/footer/footer.component'
import { useAppSelector } from '@app/store/store'

export const Wrapper = styled.div`
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  color: ${colors.textPrimary};
  background-color: ${colors.bgPage};
`

const Container = styled.div`
  max-width: 992px;
  width: 100%;
  margin: 0 auto;
  padding: 0 10px;
`

interface MainLayoutProps {
  children: ReactNode
}

export const UserProfileLayout: FC<MainLayoutProps> = ({ children }) => {
  const user = useAppSelector(state => state.userSlice.user)

  return (
    <Wrapper>
      <Header user={user} />
      <Container>{children}</Container>
      <Footer />
    </Wrapper>
  )
}

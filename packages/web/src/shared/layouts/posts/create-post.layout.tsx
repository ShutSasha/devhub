import { FC, ReactNode } from 'react'
import styled from 'styled-components'
import { Header } from '@shared/components/header/header.component'
import { Footer } from '@shared/components/footer/footer.component'
import { colors } from '@shared/consts/colors.const'

import { Wrapper } from '../main.layout'

interface CreatePosttProps {
  children: ReactNode
}

const Container = styled.div`
  max-width: 1440px;
  width: 100%;
  margin: 0 auto;
  margin-bottom: 20px;
  padding-bottom: 20px;

  background-color: ${colors.background};
  border-radius: 20px;
`

export const CreatePostLayout: FC<CreatePosttProps> = ({ children }) => {
  return (
    <Wrapper>
      <Header />
      <Container>{children}</Container>
      <Footer />
    </Wrapper>
  )
}

import { FC, ReactNode } from 'react'
import styled from 'styled-components'
import { Header } from '@shared/components/header/header.component'
import { Footer } from '@shared/components/footer/footer.component'
import { colors } from '@shared/consts/colors.const'

import { Wrapper } from '../main.layout'

interface PostViewProps {
  children: ReactNode
}

const Container = styled.div`
  max-width: 1202px;
  width: 100%;
  margin: 0 auto;
  margin-bottom: 20px;
  padding-bottom: 20px;

  border: 1px solid #767677;
  padding: 12px;
  border-radius: 20px;
`

export const PostViewLayout: FC<PostViewProps> = ({ children }) => {
  return (
    <Wrapper>
      <Header />
      <Container>{children}</Container>
      <Footer />
    </Wrapper>
  )
}
import { FC, ReactNode } from 'react'
import { Text } from '@shared/components/text/text.component'
import feedbackImage from '@assets/images/auth/feedback-man.svg'

import { FeedBack, FeedBackImg, StyledAuthForm, StyledAuthLayout, Title } from './auth-layout.style'

interface AuthLayoutProps {
  children: ReactNode
}

export const AuthLayout: FC<AuthLayoutProps> = ({ children }) => {
  return (
    <StyledAuthLayout>
      <StyledAuthForm>{children}</StyledAuthForm>
      <FeedBack>
        <Title>We’ve been using DevHub and it changed our lives!</Title>
        <FeedBackImg src={feedbackImage} alt="Feedback from man" />
        <Text text=" Wesley Peck" fontWeight="600" fontSize="20px" $lineHeight="30px" color="#fff" />
        <Text text=" Product Manager" fontWeight="400" fontSize="20px" $lineHeight="30px" color="#fff" />
      </FeedBack>
    </StyledAuthLayout>
  )
}

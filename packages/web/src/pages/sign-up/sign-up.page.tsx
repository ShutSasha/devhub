import { AuthInput } from '@shared/components/auth-input/auth-input.component'
import { AuthLayout } from '@shared/layouts/auth/auth.layout'
import { FC } from 'react'

export const SignUp: FC = () => {
  return (
    <AuthLayout>
      <AuthInput label="Username" />
    </AuthLayout>
  )
}

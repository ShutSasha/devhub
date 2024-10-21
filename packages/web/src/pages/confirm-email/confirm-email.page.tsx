import { AuthBtn } from '@shared/components/auth/btn/btn.component'
import { AuthDevhubTitle } from '@shared/components/auth/devhub-title/auth-devhub-title.components'
import { AuthInput } from '@shared/components/auth/input/auth-input.component'
import { AuthTitle } from '@shared/components/auth/title/auth-title.component'
import { AuthLayout } from '@shared/layouts/auth/auth.layout'

export const ConfirmEmail = () => {
  return (
    <AuthLayout>
      <AuthDevhubTitle />
      <AuthTitle title="Code sent to the email" style={{ marginBottom: '16px', textAlign: 'center' }} />
      <AuthInput placeholder="6-digital code" style={{ marginBottom: '16px' }} />
      <AuthBtn text="Confirm" />
    </AuthLayout>
  )
}

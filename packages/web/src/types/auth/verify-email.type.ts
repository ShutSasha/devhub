export interface VerifyEmailDto {
  readonly email: string
  readonly activationCode: string
}

export interface VerifyEmailResponse {
  message: string
}

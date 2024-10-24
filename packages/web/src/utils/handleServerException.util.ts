import { ErrorException } from '~types/error/error.type'

export const handleServerException = (error: ErrorException | undefined): string[] | undefined => {
  return error ? Object.values((error as ErrorException)?.data?.errors).flat() : undefined
}

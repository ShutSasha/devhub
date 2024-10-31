import { ErrorException } from '~types/error/error.type'

type ErrorExceptionType = ErrorException | undefined | null

export const handleServerException = (error: ErrorExceptionType): string[] | undefined => {
  if (!error || !error.data) return undefined
  return error ? Object.values((error as ErrorException)?.data?.errors).flat() : undefined
}

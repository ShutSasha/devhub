import { IUser } from '~types/user/user.type'

export interface LoginResponse {
  token: string
  user: IUser
}

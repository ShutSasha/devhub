import { IUser } from '~types/user/user.type'

export interface RefreshResponse {
  token: string
  user: IUser
}

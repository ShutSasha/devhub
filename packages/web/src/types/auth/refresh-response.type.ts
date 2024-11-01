import { IUser } from '~types/user/user.type'

export interface RefreshResponse {
  accessToken: string
  user: IUser
}

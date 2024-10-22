export interface IUser {
  _id: string
  name: string | null
  avatar: string
  email: string
  createdAt: string
  devPoints: number
  activationCode: string
  isActivated: false
  roles: string[]
}

export interface IUser {
  id: string
  userName: string
  name: string | null
  avatar: string
  email: string
  createdAt: string
  devPoints: number
  activationCode: string
  isActivated: false
  roles: string[]
  userRole: number[]
}

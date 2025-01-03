export enum ROUTES {
  HOME = '/',
  ABOUT = '/about',
  SIGN_UP = '/sign-up',
  CONFIRM_EMAIL = '/confirm-email',
  LOGIN = '/login',
  FORGOT_PASSWORD = '/forgot-password',
  FORGOT_PASSWORD_VERIFY = '/forgot-password/verify',
  ENTER_NEW_PASSWORD = '/forgot-password/verify/enter-new-password',
  CREATE_POST = '/create-post',
  EDIT_POST = '/edit-post/:id',
  POST_VIEW = '/posts/:id',
  USER_PROFILE = '/user-profile/:id',
  USER_EDIT_PROFILE = '/user-edit-profile/:id',
  USER_FRIENDS_FOLLOWERS = '/user-friends-followers/:id',
  USER_FRIENDS_FOLLOWED_LIST = '/user-friends-followers-list/:id',
  STARRED = '/starred/:id',
  CHAT = '/user-chats/:id',
  NOTIFICATION = '/notification/:id',
}

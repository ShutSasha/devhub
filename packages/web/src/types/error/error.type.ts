export interface ErrorException {
  data: {
    errors: {
      [key: string]: string[]
    }
  }
}

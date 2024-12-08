/// <reference types="vite/client" />
// vite-env.d.ts
interface ImportMetaEnv {
  VITE_API_URL: string
  VITE_GITHUB_AUTH_API: string
  VITE_GOOGLE_AUTH_API: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

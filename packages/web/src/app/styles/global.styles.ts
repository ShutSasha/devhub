import { createGlobalStyle } from 'styled-components'
import ManropeTTF from '@assets/fonts/Manrope.ttf'
import MontserratTTF from '@assets/fonts/Montserrat.ttf'
import InterTTF from '@assets/fonts/Inter.ttf'
import InterItalicTTF from '@assets/fonts/Inter-Italic.ttf'
import { FONTS } from '@shared/consts/fonts.enum'

const GlobalStyle = createGlobalStyle`
  * {
    margin: 0;
    padding: 0;
    font-family: ${FONTS.INTER}, sans-serif;
  }

  h1,
  h2,
  h3,
  h4,
  h5,
  h6 {
    margin: 0;
  }

  a {
    display: block;
    text-decoration: none;
  }

  ul, ol {
    margin: 0;
    padding: 0;
    list-style: none;
  }

  button {
    cursor: pointer;
    border: none;
    outline: none !important;
  }

  input {
    outline: none !important;
    border: none;
  }
  
  @font-face {
    font-family: ${FONTS.MANROPE};
    src: url('${ManropeTTF}');
    unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC,
      U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD;
    font-display: swap;
  }

  @font-face {
    font-family: ${FONTS.MONTSERRAT};
    src: url('${MontserratTTF}');
    unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC,
      U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD;
    font-display: swap;
  }

  @font-face {
    font-family: ${FONTS.INTER};
    src: url('${InterTTF}');
    unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC,
      U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD;
    font-display: swap;
  }
    
  @font-face {
    font-family: ${FONTS.INTER_ITALIC};
    src: url('${InterItalicTTF}');
    unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC,
      U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD;
    font-display: swap;
  }
`

export default GlobalStyle

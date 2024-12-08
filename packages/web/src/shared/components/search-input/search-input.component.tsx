import { CSSProperties, FC } from 'react'
import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'
import search from '@assets/images/search.svg'

const Container = styled.div`
  flex: 1;
  position: relative;
  margin-bottom: 16px;
`

const Input = styled.input`
  width: 100%;
  height: 38px;
  padding-left: 42px;
  box-sizing: border-box;

  color: #304050;
  font-size: 16px;
  font-family: ${FONTS.MONTSERRAT};
  line-height: 18px;
  font-weight: 500;

  border-radius: 12px;
`

const SearchIcon = styled.img`
  position: absolute;
  top: 4px;
  left: 10px;

  width: 30px;
  height: 30px;
`

interface SearchInputProps {
  placeholder?: string
  value?: string
  onChange?: () => void
  style?: CSSProperties
}

export const SearchInput: FC<SearchInputProps> = ({ placeholder, value, onChange }) => {
  return (
    <Container>
      <Input placeholder={placeholder} value={value} onChange={onChange}></Input>
      <SearchIcon src={search} />
    </Container>
  )
}

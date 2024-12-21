import { ChangeEvent, CSSProperties, FC } from 'react'
import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'
import search from '@assets/images/search.svg'

interface ContainerProps {
  isChatSearch: boolean
}

const Container = styled.div<ContainerProps>`
  flex: ${({ isChatSearch }) => (isChatSearch ? 'none' : '1')};
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
  isChatSearch: boolean
  placeholder?: string
  value?: string
  onChange?: (e: ChangeEvent<HTMLInputElement>) => void
  style?: CSSProperties
}

export const SearchInput: FC<SearchInputProps> = ({ placeholder, value, onChange,isChatSearch }) => {
  return (
    <Container isChatSearch={isChatSearch}>
      <Input placeholder={placeholder} value={value} onChange={onChange}></Input>
      <SearchIcon src={search} />
    </Container>
  )
}

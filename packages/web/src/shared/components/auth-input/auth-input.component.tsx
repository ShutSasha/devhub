import { FONTS } from '@shared/consts/fonts.enum'
import { FC } from 'react'
import styled from 'styled-components'

const InputContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
`

const Span = styled.span`
  font-family: ${FONTS.MANROPE};
  font-size: 16px;
  line-height: 24px;
  font-weight: 400px;
  color: #304050;
`

const Input = styled.input`
  width: 100%;
  border: 1px solid #8d96a0;
  border-radius: 5px;
  height: 40px;
  padding-left: 10px;
  font-family: ${FONTS.MANROPE};
  font-size: 16px;
  line-height: 24px;
  font-weight: 400;
  color: #304050;
  box-sizing: border-box;
`

interface AuthInputProps {
  label: string
}

export const AuthInput: FC<AuthInputProps> = ({ label }) => {
  return (
    <InputContainer>
      <Span>{label}</Span>
      <Input />
    </InputContainer>
  )
}

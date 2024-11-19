import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'

export const UserEditProfileContainer = styled.div`
  display: grid;
  gap: 20px;
  grid-template-columns: 1fr 2fr;
  align-items: start;
  margin-top: 120px;
  margin-bottom: 120px;
`

export const UserOptionsContainer = styled.div`
  background-color: #2a2a2d;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  border-radius: 15px;
`
export const UserOptionsInnerContainer = styled.div<{ $currentPage?: boolean }>`
  border-radius: 10px;
  display: flex;
  align-items: center;
  padding: 6px 10px;
  gap: 18px;
  background-color: ${({ $currentPage }) => ($currentPage ? '#F7971D' : '')};
`

export const OptionsText = styled.p`
  color: #fff;
  font-size: 20px;
  font-family: ${FONTS.INTER};
  font-weight: 600;
`

export const UserEditCredentionalsContainer = styled.div`
  border-radius: 15px;
  background-color: #2a2a2d;
  display: flex;
  flex-direction: column;
  gap: 18px;
  margin-bottom: 20px;
  padding: 35px 58px;
`

export const CredentionalsTitle = styled.p`
  color: #fff;
  font-size: 24px;
  line-height: 36px;
  font-family: ${FONTS.MANROPE};
  font-weight: 700;
  margin-bottom: 15px;
`

export const InputContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 4px;
`

export const Label = styled.label`
  font-size: 16px;
  color: #fff;
  margin-bottom: 8px;
  display: block;
`

export const Input = styled.input`
  width: 100%;
  padding: 10px 14px;
  font-size: 16px;
  color: #fff;
  background-color: transparent;
  border: 2px solid #f7971d;
  border-radius: 8px;
  outline: none;

  ::placeholder {
    color: #aaa;
  }

  &:focus {
    border-color: #fff;
  }
`

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
`

export const FileUploadContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 10px;
`

export const ImagePreview = styled.img`
  width: 38px;
  height: 38px;
  border-radius: 50%;
  object-fit: cover;
`

export const FileInputLabel = styled.label`
  display: inline-block;
  padding: 6px 12px;
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  background-color: transparent;
  border: 2px solid #f7971d;
  border-radius: 8px;
  cursor: pointer;
  text-align: center;

  &:hover {
    background-color: #f7971d;
    color: #000;
  }
`

export const HiddenFileInput = styled.input`
  display: none;
`

export const FileName = styled.span`
  font-size: 14px;
  color: #fff;
`

export const SaveButton = styled.button`
  width: 100%;
  padding: 12px 20px;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  background-color: #f7971d;
  border: none;
  border-radius: 8px;
  cursor: pointer;

  &:hover {
    background-color: #e68616;
  }

  &:active {
    background-color: #c76c13;
  }
`

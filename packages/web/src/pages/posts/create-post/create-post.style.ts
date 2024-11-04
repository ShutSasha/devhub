import { colors } from '@shared/consts/colors.const'
import { FONTS } from '@shared/consts/fonts.enum'
import styled from 'styled-components'

export const CreatePostContainer = styled.div`
  margin: 0 30px;
`

export const Title = styled.p`
  font-family: ${FONTS.MONTSERRAT};
  font-size: 32px;
  line-height: 38px;
  text-align: center;
  font-weight: 600;

  color: #ededed;
  padding: 6px 0;
`

export const EmphasizeLine = styled.hr`
  height: 2px;
  width: 100%;
  background-color: #fff;

  box-sizing: border-box;
  margin-bottom: 20px;
`

export const UploadImageContainer = styled.div<{ $image: string | undefined; $width: string; $height: string }>`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  min-height: ${({ $height }) => $height};

  margin-bottom: 20px;

  cursor: pointer;
  border-radius: 10px;
  border: ${({ $image }) => ($image ? `none` : `1px dashed ${colors.textSecondary}`)};

  background: ${({ $image }) => ($image ? `url(${$image})` : '')};
  background-repeat: no-repeat;

  background-size: cover;
  background-position: 50% 0%;
  box-sizing: border-box;

  width: ${({ $width }) => $width};
  position: relative;
`

export const UploadInput = styled.input`
  opacity: 0;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
`

export const UploadImage = styled.img`
  height: 32px;
  width: 32px;
  margin-bottom: 5px;

  object-fit: cover;
`

export const PostTitleInput = styled.input`
  display: block;
  width: 100%;
  background: transparent;

  font-family: ${FONTS.MONTSERRAT};
  font-size: 34px;
  line-height: 42px;
  font-weight: 700;
  color: #ededed;

  margin-bottom: 30px;

  &:placeholder {
    color: ${colors.black300};
  }
`

export const TagInput = styled.input`
  height: 44px;
  max-width: 200px;
  width: 100%;

  color: ${colors.black600};
  background: transparent;
  font-family: ${FONTS.MONTSERRAT};
  font-size: 22px;
  line-height: 28px;
  font-weight: 500;
  border: 1px solid #ededed;
  border-radius: 10px;

  padding: 0 10px;
  margin-bottom: 30px;
`

export const BtnContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`

export const PublishBtn = styled.button`
  background: ${colors.bgPage};
  font-family: ${FONTS.INTER};
  border-radius: 8px;
  color: #ededed;
  font-size: 28px;
  line-height: 34px;
  font-weight: 600;
  padding: 10px 14px;
  border: none;

  cursor: pointer;
  transition: all 0.2s ease-in;

  &:hover {
    background: #ededed;
    color: ${colors.bgPage};
  }
`

import { useRef } from 'react'
import { CreatePostLayout } from '@shared/layouts/posts/create-post.layout'
import { StyledAvatar, StyledUserCredentialsContainer, Username } from '@shared/components/post/post.style'
import { useAppSelector } from '@app/store/store'
import { Text } from '@shared/components/text/text.component'
import { FONTS } from '@shared/consts/fonts.enum'
import { colors } from '@shared/consts/colors.const'
import uploadSvg from '@assets/images/post/upload.svg'

import * as S from './create-post.style'
import { InputContainer } from './components/editable-area.component'

export const CreatePost = () => {
  const content = useRef<HTMLSpanElement>(null)

  const user = useAppSelector(state => state.userSlice.user)

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (file) {
      console.log('file here:', file)
    }
  }

  const handlePublish = () => {
    console.log(content.current?.textContent)
  }

  return (
    <CreatePostLayout>
      <S.Title>Create post</S.Title>
      <S.EmphasizeLine />
      <S.CreatePostContainer>
        <StyledUserCredentialsContainer style={{ marginLeft: '20px', marginBottom: '20px' }}>
          <StyledAvatar style={{ height: '60px', width: '60px' }} src={user?.avatar} />
          <Username style={{ fontSize: '26px', lineHeight: '36px', fontWeight: '500' }}>{user?.userName}</Username>
        </StyledUserCredentialsContainer>
        <S.UploadImageContainer>
          <S.UploadImage src={uploadSvg} />
          <Text
            text="Press to add background"
            fontFamily={FONTS.MONTSERRAT}
            fontSize="26px"
            fontWeight="500"
            $lineHeight="32px"
            color={colors.textSecondary}
          />
          <S.UploadInput type="file" onChange={handleFileUpload} />
        </S.UploadImageContainer>
        <S.PostTitleInput placeholder="Title here" />
        <S.TagInput type="text" placeholder="Add tags here" />
        <InputContainer ref={content} />
        <S.EmphasizeLine />
        <S.BtnContainer>
          <S.PublishBtn onClick={handlePublish}>Publish</S.PublishBtn>
        </S.BtnContainer>
      </S.CreatePostContainer>
    </CreatePostLayout>
  )
}

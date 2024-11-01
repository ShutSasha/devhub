import { ChangeEvent, useMemo, useRef, useState } from 'react'
import { CreatePostLayout } from '@shared/layouts/posts/create-post.layout'
import { StyledAvatar, StyledUserCredentialsContainer, Username } from '@shared/components/post/post.style'
import { useAppSelector } from '@app/store/store'
import { Text } from '@shared/components/text/text.component'
import { FONTS } from '@shared/consts/fonts.enum'
import { colors } from '@shared/consts/colors.const'
import uploadSvg from '@assets/images/post/upload.svg'
import { useCreatePostMutation } from '@api/post.api'

import { InputContainer } from './components/editable-area.component'
import * as S from './create-post.style'

export const CreatePost = () => {
  const content = useRef<HTMLSpanElement>(null)
  const [createPost] = useCreatePostMutation()
  const user = useAppSelector(state => state.userSlice.user)
  const [headerImage, setHeaderImage] = useState<File>()
  const [title, setTitle] = useState<string>('')
  const [linkImage, setLinkImage] = useState<string | undefined>('')

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (file) {
      setHeaderImage(file)
    }
  }

  const handleChangeTitle = (e: ChangeEvent<HTMLInputElement>) => {
    setTitle(e.target.value)
  }

  const convertToBase64 = (file: File): Promise<string> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = () => resolve(reader.result as string)
      reader.onerror = error => reject(error)
    })
  }

  const handlePublish = async () => {
    try {
      const base64Image = headerImage ? await convertToBase64(headerImage) : ''

      const postData = {
        content: content.current?.textContent || '',
        title,
        userId: user?.id || '',
        tags: ['1', '2'],
        headerImage: base64Image,
      }

      await createPost(postData).unwrap()
    } catch (e) {
      console.error(e)
    }
  }

  useMemo(() => {
    setLinkImage(headerImage ? URL.createObjectURL(headerImage) : undefined)
  }, [headerImage])

  return (
    <CreatePostLayout>
      <S.Title>Create post</S.Title>
      <S.EmphasizeLine />
      <S.CreatePostContainer>
        <StyledUserCredentialsContainer style={{ marginLeft: '20px', marginBottom: '20px' }}>
          <StyledAvatar style={{ height: '60px', width: '60px' }} src={user?.avatar} />
          <Username style={{ fontSize: '26px', lineHeight: '36px', fontWeight: '500' }}>{user?.userName}</Username>
        </StyledUserCredentialsContainer>
        <S.UploadImageContainer $image={linkImage}>
          {!headerImage && (
            <>
              <S.UploadImage src={uploadSvg} />
              <Text
                text="Press to add background"
                fontFamily={FONTS.MONTSERRAT}
                fontSize="26px"
                fontWeight="500"
                $lineHeight="32px"
                color={colors.textSecondary}
              />
            </>
          )}
          <S.UploadInput type="file" onChange={handleFileUpload} />
        </S.UploadImageContainer>
        <S.PostTitleInput placeholder="Title here" value={title} onChange={handleChangeTitle} />
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

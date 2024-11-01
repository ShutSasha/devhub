import { ChangeEvent, useRef, useState } from 'react'
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

import { PostDto } from '~types/post/post.dto'

export const CreatePost = () => {
  const content = useRef<HTMLSpanElement>(null)
  const [createPost] = useCreatePostMutation()
  const user = useAppSelector(state => state.userSlice.user)
  const [headerImage, setHeaderImage] = useState<File>()
  const [headerImageUrl, setHeaderImageUrl] = useState<string | null>(null)
  const [title, setTitle] = useState<string>('')

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (file) {
      setHeaderImage(file)
      const reader = new FileReader()
      reader.onloadend = () => {
        setHeaderImageUrl(reader.result as string)
      }
      reader.readAsDataURL(file)
    }
  }

  const handleChangeTitle = (e: ChangeEvent<HTMLInputElement>) => {
    setTitle(e.target.value)
  }

  const createFormData = (data: PostDto): FormData => {
    const formData = new FormData()
    formData.append('content', data.content)
    formData.append('title', data.title)
    formData.append('userId', data.userId)
    formData.append('tags', JSON.stringify(data.tags))
    formData.append('headerImage', data.headerImage)

    return formData
  }

  const handlePublish = async () => {
    try {
      const postData: PostDto = {
        content: content.current?.textContent || '',
        title,
        userId: user?.id || '',
        tags: ['1', '2'],
        headerImage: headerImage || '',
      }

      const formData = createFormData(postData)

      const res = await createPost(formData).unwrap()
      console.log(res)
    } catch (e) {
      console.error(e)
    }
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
        <S.UploadImageContainer $image={headerImageUrl || ''}>
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

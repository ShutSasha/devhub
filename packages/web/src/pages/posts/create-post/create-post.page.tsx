import { ChangeEvent, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import { CreatePostLayout } from '@shared/layouts/posts/create-post.layout'
import { StyledAvatar, StyledUserCredentialsContainer, Username } from '@shared/components/post/post.style'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import { Text } from '@shared/components/text/text.component'
import { FONTS } from '@shared/consts/fonts.enum'
import { colors } from '@shared/consts/colors.const'
import uploadSvg from '@assets/images/post/upload.svg'
import { useCreatePostMutation, useLazyGetPostsQuery } from '@api/post.api'
import { ROUTES } from '@pages/router/routes.enum'
import { handleServerException } from '@utils/handleServerException.util'
import { setLoading, setPosts } from '@features/posts/posts.slice'

import { InputContainer } from './components/editable-area.component'
import * as S from './create-post.style'

import { PostDto } from '~types/post/post.dto'
import { ErrorException } from '~types/error/error.type'

export const CreatePost = () => {
  const navigate = useNavigate()
  const content = useRef<HTMLSpanElement>(null)
  const [createPost] = useCreatePostMutation()
  const [getPosts] = useLazyGetPostsQuery()
  const dispatch = useAppDispatch()
  const user = useAppSelector(state => state.userSlice.user)
  const [headerImage, setHeaderImage] = useState<File>()
  const [headerImageUrl, setHeaderImageUrl] = useState<string | null>(null)
  const [headerImageWidth, setHeaderImageWidth] = useState<string>('100%')
  const [headerImageHeight, setHeaderImageHeight] = useState<string>('420px')
  const [isDisableBtn, setIsDisableBtn] = useState<boolean>(false)

  const [tags, setTags] = useState<string>('')
  const [title, setTitle] = useState<string>('')

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (file) {
      setHeaderImage(file)

      const reader = new FileReader()
      reader.onloadend = () => {
        const imageUrl = reader.result as string
        setHeaderImageUrl(imageUrl)

        const img = new Image()
        img.src = imageUrl

        img.onload = () => {
          if (img.width < 1400) {
            setHeaderImageWidth(`${img.width}px`)
          } else {
            setHeaderImageWidth('100%')
          }

          if (img.height < 800) {
            setHeaderImageHeight(`${img.height}px`)
          } else {
            setHeaderImageHeight('600px')
          }
        }
      }
      reader.readAsDataURL(file)
    }
  }

  const handleChangeTitle = (e: ChangeEvent<HTMLInputElement>) => {
    setTitle(e.target.value)
  }

  const handleChangeTags = (e: ChangeEvent<HTMLInputElement>) => {
    setTags(e.target.value)
  }

  const createFormData = (postDto: PostDto): FormData => {
    const formData = new FormData()
    formData.append('content', postDto.content)
    formData.append('title', postDto.title)
    formData.append('userId', postDto.userId)
    formData.append('tags', JSON.stringify(postDto.tags))
    formData.append('headerImage', postDto.headerImage)

    return formData
  }

  const handlePublish = async () => {
    try {
      setIsDisableBtn(true)

      const postData: PostDto = {
        content: content.current?.textContent || '',
        title,
        userId: user?._id || '',
        tags: tags
          .trim()
          .split(',')
          .map(tag => tag.trim()),
        headerImage: headerImage || '',
      }

      const formData = createFormData(postData)
      dispatch(setLoading(true))
      await createPost(formData).unwrap()

      navigate(ROUTES.HOME)
    } catch (e) {
      console.error(e)
      toast.error(handleServerException(e as ErrorException)?.join(', '))
    } finally {
      setIsDisableBtn(false)
      const { data } = await getPosts()
      dispatch(setPosts(data || null))
      dispatch(setLoading(false))
    }
  }

  return (
    <CreatePostLayout>
      <S.Title>Create post</S.Title>
      <S.EmphasizeLine />
      <S.CreatePostContainer>
        <StyledUserCredentialsContainer style={{ marginLeft: '20px', marginBottom: '20px' }}>
          <StyledAvatar style={{ height: '60px', width: '60px' }} src={user?.avatar} />
          <Username style={{ fontSize: '26px', lineHeight: '36px', fontWeight: '500' }}>{user?.username}</Username>
        </StyledUserCredentialsContainer>
        <S.UploadImageContainer $image={headerImageUrl || ''} $width={headerImageWidth} $height={headerImageHeight}>
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
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '30px' }}>
          <S.TagInput type="text" placeholder="Add tags here" value={tags} onChange={handleChangeTags} />
          <Text
            text="maximum 4 tags (tags are separated by commas)"
            color={colors.black600}
            fontSize="20px"
            fontWeight="500"
            $lineHeight="24px"
          />
        </div>
        <InputContainer ref={content} />
        <S.EmphasizeLine />
        <S.BtnContainer>
          <S.PublishBtn onClick={handlePublish} disabled={isDisableBtn}>
            Publish
          </S.PublishBtn>
        </S.BtnContainer>
      </S.CreatePostContainer>
    </CreatePostLayout>
  )
}

import { ChangeEvent, useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { UserProfileLayout } from '@shared/layouts/user/user-profile.layout'
import { useAppDispatch, useAppSelector } from '@app/store/store'
import userSVG from '@assets/images/user/user-ic.svg'
import { useEditUserDataMutation, useEditUserPhotoMutation } from '@api/user.api'
import { setUserAvatar } from '@features/user/user.slice'

import * as _ from './edit-profile.style'

export const UserEditProfile = () => {
  const dispatch = useAppDispatch()
  const user = useAppSelector(state => state.userSlice.user)
  const [file, setFile] = useState<File | null>(null)
  const [preview, setPreview] = useState<string | undefined>(user?.avatar)
  const [editUserPhoto, { isLoading }] = useEditUserPhotoMutation()
  const [editUserData, { isLoading: isLoadingUserData }] = useEditUserDataMutation()
  const [name, setName] = useState<string>(user?.name || '')
  const [bio, setBio] = useState<string>(user?.bio || '')

  useEffect(() => {
    if (user) {
      setName(user.name || '')
      setBio(user.bio || '')
    }
  }, [user])

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const selectedFile = e.target.files[0]
      setFile(selectedFile)
      setPreview(URL.createObjectURL(selectedFile))

      const formData = new FormData()
      formData.append('file', selectedFile)

      try {
        const { avatar } = await editUserPhoto({ id: user?._id, body: formData }).unwrap()
        dispatch(setUserAvatar(avatar))
        toast.success('Photo updated successfully!', { autoClose: 2000 })
      } catch (error) {
        console.error('Failed to update photo:', error)
        toast.error('failed to upload image')
      }
    }
  }

  const handleNameInput = (e: ChangeEvent<HTMLInputElement>) => {
    setName(e.target.value)
  }

  const handleBioInput = (e: ChangeEvent<HTMLInputElement>) => {
    setBio(e.target.value)
  }

  const handleChangeUserData = async () => {
    try {
      await editUserData({ id: user?._id, name, bio }).unwrap()
      toast.success('User has been updated')
    } catch (e) {
      console.error(e)
      toast.error('smth went wrong')
    }
  }

  return (
    <UserProfileLayout>
      <_.UserEditProfileContainer>
        <_.UserOptionsContainer>
          <_.UserOptionsInnerContainer $currentPage={true}>
            <img style={{ width: '20px', height: '20px' }} src={userSVG} />
            <_.OptionsText>Profile</_.OptionsText>
          </_.UserOptionsInnerContainer>
        </_.UserOptionsContainer>
        <_.UserEditCredentionalsContainer>
          <_.CredentionalsTitle>User</_.CredentionalsTitle>
          <_.InputContainer>
            <_.Label htmlFor="name">Name</_.Label>
            <_.Input value={name} onChange={handleNameInput} type="text" placeholder="Enter your name" />
          </_.InputContainer>
          <_.InputContainer>
            <_.Label htmlFor="describtion">Description</_.Label>
            <_.Input value={bio} onChange={handleBioInput} type="text" placeholder="Describe yourself" />
          </_.InputContainer>
          <_.Container>
            <_.FileUploadContainer>
              <_.ImagePreview src={preview || user?.avatar} alt="Profile" />
              <_.FileInputLabel htmlFor="file-upload">{isLoading ? 'Uploading...' : 'Choose file'}</_.FileInputLabel>
              <_.HiddenFileInput id="file-upload" type="file" onChange={handleFileChange} disabled={isLoading} />
              <_.FileName>{file ? file.name : 'File not chosen'}</_.FileName>
            </_.FileUploadContainer>
            <_.SaveButton onClick={handleChangeUserData} disabled={isLoadingUserData}>
              {isLoadingUserData ? 'Saving...' : 'Save changes'}
            </_.SaveButton>
          </_.Container>
        </_.UserEditCredentionalsContainer>
      </_.UserEditProfileContainer>
    </UserProfileLayout>
  )
}

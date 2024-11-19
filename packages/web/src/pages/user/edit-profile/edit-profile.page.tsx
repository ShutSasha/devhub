import { useState } from 'react'
import { toast } from 'react-toastify'
import { UserProfileLayout } from '@shared/layouts/user/user-profile.layout'
import { useAppSelector } from '@app/store/store'
import userSVG from '@assets/images/user/user-ic.svg'
import { useEditUserDataMutation, useEditUserPhotoMutation } from '@api/user.api'

import * as _ from './edit-profile.style'

export const UserEditProfile = () => {
  const user = useAppSelector(state => state.userSlice.user)
  const [file, setFile] = useState<File | null>(null)
  const [preview, setPreview] = useState<string | undefined>(user?.avatar)
  const [editUserPhoto, { isLoading }] = useEditUserPhotoMutation()
  const [editUserData] = useEditUserDataMutation()

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const selectedFile = e.target.files[0]
      setFile(selectedFile)
      setPreview(URL.createObjectURL(selectedFile))

      const formData = new FormData()
      formData.append('file', selectedFile)

      try {
        await editUserPhoto({ id: user?._id, body: formData }).unwrap()
        toast.success('Photo updated successfully!')
      } catch (error) {
        console.error('Failed to update photo:', error)
        toast.error('failed to upload image')
      }
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
            <_.Input type="text" placeholder="Enter your name" />
          </_.InputContainer>
          <_.InputContainer>
            <_.Label htmlFor="name">Description</_.Label>
            <_.Input type="text" placeholder="Describe yourself" />
          </_.InputContainer>
          <_.Container>
            <_.FileUploadContainer>
              <_.ImagePreview src={preview || user?.avatar} alt="Profile" />
              <_.FileInputLabel htmlFor="file-upload">{isLoading ? 'Uploading...' : 'Choose file'}</_.FileInputLabel>
              <_.HiddenFileInput id="file-upload" type="file" onChange={handleFileChange} disabled={isLoading} />
              <_.FileName>{file ? file.name : 'File not chosen'}</_.FileName>
            </_.FileUploadContainer>
            <_.SaveButton disabled={isLoading}>{isLoading ? 'Saving...' : 'Save changes'}</_.SaveButton>
          </_.Container>
        </_.UserEditCredentionalsContainer>
      </_.UserEditProfileContainer>
    </UserProfileLayout>
  )
}

import styled from 'styled-components'

export const PostsContainer = styled.div`
  & > div:last-child {
    margin-bottom: 20px;
  }
`

export const DropdownContainer = styled.div`
  position: relative;
  display: inline-block;
`

export const DropdownButton = styled.button`
  background-color: #333;
  color: #ffa500;
  border: none;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
`

export const DropdownContent = styled.div<{ $isOpen: boolean }>`
  display: ${(props: { $isOpen: boolean }) => (props.$isOpen ? 'block' : 'none')};
  position: absolute;
  background-color: #222;
  color: white;
  min-width: 200px;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1;
`

export const Option = styled.label`
  display: flex;
  align-items: center;
  padding: 5px 0;
  font-size: 14px;
  cursor: pointer;

  input {
    margin-right: 10px;
  }
`

export const ApplyButton = styled.button`
  background-color: #ffa500;
  color: #fff;
  border: none;
  padding: 8px 12px;
  cursor: pointer;
  margin-top: 10px;
  width: 100%;
  border-radius: 4px;

  &:hover {
    background-color: #ff9200;
  }
`

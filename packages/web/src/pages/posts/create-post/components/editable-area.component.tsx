import { FC, ForwardedRef, forwardRef, useState } from 'react'
import styled from 'styled-components'

const EditableDiv = styled.span`
  display: block;
  font-size: 20px;
  line-height: 1.5;
  font-weight: 500;
  min-height: 500px;
  outline: none;
  white-space: pre-wrap;
  word-break: break-word;
  padding-bottom: 20px;
`

const Placeholder = styled.span`
  color: #aaa;
  user-select: none;
`

export const InputContainer = forwardRef<HTMLSpanElement, React.PropsWithChildren<{}>>(
  (props, ref: ForwardedRef<HTMLSpanElement>) => {
    const [isPlaceholderVisible, setPlaceholderVisible] = useState(true)

    const handleInput = () => {
      const text = (ref as React.MutableRefObject<HTMLSpanElement | null>).current?.textContent || ''
      setPlaceholderVisible(text === '')
    }

    return (
      <EditableDiv ref={ref} contentEditable onInput={handleInput} suppressContentEditableWarning={true}>
        {isPlaceholderVisible && <Placeholder>Enter your text here...</Placeholder>}
      </EditableDiv>
    )
  },
)

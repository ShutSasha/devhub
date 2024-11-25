import { FONTS } from '@shared/consts/fonts.enum'
import { ForwardedRef, forwardRef, useState } from 'react'
import styled from 'styled-components'

const EditableDiv = styled.span<{ $isPlaceholderVisible: boolean }>`
  display: block;
  font-size: 20px;
  line-height: 1.5;
  font-weight: 500;
  min-height: 500px;
  outline: none;
  white-space: pre-wrap;
  word-break: break-word;
  padding-bottom: 20px;
  font-family: ${FONTS.MANROPE};

  &::before {
    content: ${({ $isPlaceholderVisible }) => ($isPlaceholderVisible ? '"Enter your text here..."' : '""')};
    color: #aaa;
    pointer-events: none;
    user-select: none;
  }
`

export const InputContainer = forwardRef<HTMLSpanElement, React.PropsWithChildren<{}>>(
  (props, ref: ForwardedRef<HTMLSpanElement>) => {
    const [isPlaceholderVisible, setPlaceholderVisible] = useState(true)

    const handleInput = () => {
      const text = (ref as React.MutableRefObject<HTMLSpanElement | null>).current?.textContent || ''
      setPlaceholderVisible(text.trim() === '')
    }

    return (
      <EditableDiv
        ref={ref}
        contentEditable
        $isPlaceholderVisible={isPlaceholderVisible}
        onInput={handleInput}
        suppressContentEditableWarning={true}
      />
    )
  },
)

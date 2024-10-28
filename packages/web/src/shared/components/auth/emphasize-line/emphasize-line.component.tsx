import { FC } from 'react'
import styled, { CSSProperties } from 'styled-components'

const StyledHr = styled.hr`
  color: red;
  background-color: rgba(48, 64, 80, 0.15);
  height: 1px;
  border: none;
  margin-bottom: 16px;
`

interface EmphasizeLineProps {
  style?: CSSProperties
}

export const EmphasizeLine: FC<EmphasizeLineProps> = ({ style }) => <StyledHr style={{ ...style }} />

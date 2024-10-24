import styled from 'styled-components'

const StyledErrorSpan = styled.span`
  display: block;
  color: red;
  margin-bottom: 8px;
`

export const ErrorSpan = ({ value }: { value: string }) => {
  return <StyledErrorSpan>{value}</StyledErrorSpan>
}

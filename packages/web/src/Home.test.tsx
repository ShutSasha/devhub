import { Home } from '@pages/home/home.page'
import { render, screen } from '@testing-library/react'

test('renders learn react link', () => {
  render(<Home />)
  const linkElement = screen.getByText(/home/i)
  expect(linkElement).toBeInTheDocument()
})

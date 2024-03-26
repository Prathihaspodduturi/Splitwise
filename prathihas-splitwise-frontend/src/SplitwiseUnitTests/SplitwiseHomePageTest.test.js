import { render, screen, fireEvent } from '@testing-library/react';
import SplitwiseHomePage from '../SplitWiseComponents/SplitwiseHomePage';
import { BrowserRouter as Router } from 'react-router-dom';
import { MemoryRouter } from 'react-router-dom';

test("Testing text for SplitwiseHomePage", () => {
    render(
        <MemoryRouter>
            <SplitwiseHomePage/>
        </MemoryRouter>
    );

    const welcomeText = screen.getByText(/Welcome to My Splitwise/i);

    const introText = screen.getByText(/A simple site to split ans maintain expenses/i);

    const logOrSignupText = screen.getByText(/Please log in or sign up to use the splitwise app./i);

    expect(welcomeText).toBeInTheDocument();
    expect(introText).toBeInTheDocument();
    expect(logOrSignupText).toBeInTheDocument();
})

describe('SplitwiseHomePage NavLink Tests', () => {
    test('renders Login and Signup NavLinks when not logged in', () => {
      render(
        <MemoryRouter>
          <SplitwiseHomePage />
        </MemoryRouter>
      );
  
      const loginLink = screen.getByRole('link', { name: /login/i });
      expect(loginLink).toHaveAttribute('href', '/splitwise-login');
  
      const signupLinks = screen.getAllByText(/sign up/i);
      // Assuming the signup link you want to test is the first one returned
      const signupLink = signupLinks.find(link => link.getAttribute('href') === '/splitwise-signup');
      expect(signupLink).toBeInTheDocument();
    });
  });


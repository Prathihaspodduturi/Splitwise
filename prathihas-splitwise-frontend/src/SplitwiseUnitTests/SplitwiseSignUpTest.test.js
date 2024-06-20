import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { BrowserRouter, MemoryRouter } from "react-router-dom";
import SplitwiseSignupPage from "../SplitWiseComponents/SplitwiseSignupPage";

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'), // use actual for all non-hook parts
    useNavigate: () => mockNavigate,
  }));

  beforeAll(() => {
    global.fetch = jest.fn();
    Object.defineProperty(window, 'sessionStorage', {
      value: {
        getItem: jest.fn(),
        setItem: jest.fn(),
        removeItem: jest.fn(),
        clear: jest.fn(),
      },
      writable: true
    });
  });

  beforeEach(() => {
    fetch.mockClear();
    sessionStorage.clear();
    mockNavigate.mockClear();
  });

  describe("SignUpPageTesting", () => {

    test("navigate to the login page on clicking 'Login'", async() => {
        render(<SplitwiseSignupPage/>, {wrapper: BrowserRouter});

        expect(screen.getByText(/Already a user:/i)).toBeInTheDocument();

        const LoginLink = screen.getByText(/Login here/i).closest('span');

        // Simulate a click on the signup link
        fireEvent.click(LoginLink);

        await waitFor(() => {
            // Check if navigation was called with the correct path
          expect(mockNavigate).toHaveBeenCalledWith('/splitwise/login');
        });
    })

    test("redirects if already logged in", async () => {

        // Mock the sessionStorage to simulate a logged in user
        sessionStorage.getItem.mockImplementation((key) => {
            if (key === "token") return "dummy-token";
            return null;
          });
    
        render(<SplitwiseSignupPage/>, { wrapper: MemoryRouter });
    
        // expecting navigation 
        expect(mockNavigate).toHaveBeenCalledTimes(1);
        expect(mockNavigate).toHaveBeenCalledWith('/splitwise/groups');
      });

      test("handles user input and form submission successfully", async () => {
    
        //Mock fetch for intial connection check
        fetch.mockResolvedValueOnce({
          ok: true,
          text: () => Promise.resolve("Signup successfull"),
        });
    
        render(<SplitwiseSignupPage/>, { wrapper: MemoryRouter });
    
        // Simulate user input and form submission
        fireEvent.change(screen.getByLabelText("Username:"), { target: { value: "Chikku" } });
        fireEvent.change(screen.getByLabelText("Password:"), { target: { value: "test123"}});

        // simulate form submission
        fireEvent.click(screen.getByRole("button", { name: /Sign Up/i }));
        
        await waitFor(() => {
            expect(screen.getByText("Signup successfull")).toBeInTheDocument();
          });
      });

      test("failed singup", async () => {
        fetch.mockResolvedValueOnce({
            ok: false,
            text: () => Promise.resolve("user already exists"), // You might adjust based on actual error handling
          });

        render(<SplitwiseSignupPage/>, { wrapper: MemoryRouter });
    
        // Simulate user input and form submission
        fireEvent.change(screen.getByLabelText("Username:"), { target: { value: "Chikku" } });
        fireEvent.change(screen.getByLabelText("Password:"), { target: { value: "test123"}});

        // simulate form submission
        fireEvent.click(screen.getByRole("button", { name: /Sign Up/i }));

        await waitFor(() => {
            expect(screen.getByText("user already exists")).toBeInTheDocument();
        });
      });

      test("failed to connect", async () => {
        fetch.mockRejectedValueOnce(new Error("Unable to connect to the server. Please try again later."));

        render(<SplitwiseSignupPage/>, { wrapper: MemoryRouter });
    
        // Simulate user input and form submission
        fireEvent.change(screen.getByLabelText("Username:"), { target: { value: "Chikku" } });
        fireEvent.change(screen.getByLabelText("Password:"), { target: { value: "test123"}});

        // simulate form submission
        fireEvent.click(screen.getByRole("button", { name: /Sign Up/i }));

        await waitFor(() => {
            expect(screen.getByText("Unable to connect to the server. Please try again later.")).toBeInTheDocument();
        });
      });

  })
import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { BrowserRouter, MemoryRouter } from "react-router-dom";
import SplitwiseLoginPage from "../SplitWiseComponents/SplitwiseLoginPage";

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

  describe("Testing SplitwiseLoginPage", () => {

    test("testing whether content is shown intitally when page is loaded", () => {

        render(
            <BrowserRouter>
                <SplitwiseLoginPage/>
            </BrowserRouter>
        );

        expect(screen.getByText(/please login to your account/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/username/i)).toBeInTheDocument();  
        expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /Log In/i })).toBeInTheDocument();
        expect(screen.getByText(/Don't have an account/i)).toBeInTheDocument();

    });

    test("navigates to the signup page on clicking 'SignUp'", async () => {
        render(<SplitwiseLoginPage/>, { wrapper: BrowserRouter });
    
        expect(screen.getByText(/don't have an account/i)).toBeInTheDocument();
        // Find the signup link
        const signUpLink = screen.getByText(/Sign Up/i).closest('span');
    
        // Simulate a click on the signup link
        fireEvent.click(signUpLink);
    
        await waitFor(() => {
            // Check if navigation was called with the correct path
          expect(mockNavigate).toHaveBeenCalledWith('/splitwise/signup');
        });
    });

    test("redirects if already logged in", async () => {

        // Mock the sessionStorage to simulate a logged in user
        sessionStorage.getItem.mockImplementation((key) => {
            if (key === "token") return "dummy-token";
            return null;
          });
    
        render(<SplitwiseLoginPage/>, { wrapper: MemoryRouter });
    
        // expecting navigation 
        expect(mockNavigate).toHaveBeenCalledTimes(1);
        expect(mockNavigate).toHaveBeenCalledWith('/splitwise/groups');
      });

      test("handles user input and form submission successfully", async () => {
    
        //Mock fetch for intial connection check
        fetch.mockResolvedValueOnce({
          ok: true,
          text: () => Promise.resolve("fake-jwt-token"),
        });
    
        render(<SplitwiseLoginPage/>, { wrapper: MemoryRouter });
    
        // Simulate user input and form submission
        fireEvent.change(screen.getByLabelText("Username:"), { target: { value: "Chikku" } });
        fireEvent.change(screen.getByLabelText("Password:"), { target: { value: "test123"}});

        // simulate form submission
        fireEvent.click(screen.getByRole("button", { name: /Log In/i }));
        
        await waitFor(() => {

            // Verify that sessionStorage.setItem was called with expected token

            expect(sessionStorage.setItem).toHaveBeenCalledTimes(3);
            expect(sessionStorage.setItem).toHaveBeenCalledWith("token", "fake-jwt-token");
            expect(sessionStorage.setItem).toHaveBeenCalledWith("Connected", true);
            expect(sessionStorage.setItem).toHaveBeenCalledWith("username", "Chikku");
        
            // Verify navigation to home page after successful login
            expect(mockNavigate).toHaveBeenCalledTimes(1);
            expect(mockNavigate).toHaveBeenCalledWith('/splitwise/groups');
          });

    });


    test("failed login", async () => {
        fetch.mockResolvedValueOnce({
            ok: false,
            text: () => Promise.resolve("Bad Credentials"), // You might adjust based on actual error handling
          });

        render(<SplitwiseLoginPage/>, { wrapper: MemoryRouter });
    
        // Simulate user input and form submission
        fireEvent.change(screen.getByLabelText("Username:"), { target: { value: "Chikku" } });
        fireEvent.change(screen.getByLabelText("Password:"), { target: { value: "test123"}});

        // simulate form submission
        fireEvent.click(screen.getByRole("button", { name: /Log In/i }));

        await waitFor(() => {
            expect(screen.getByText("Bad Credentials")).toBeInTheDocument();
        });
      });

      test("failed to connect", async () => {
        fetch.mockRejectedValueOnce(new Error("Unable to connect to the server. Please try again later."));

        render(<SplitwiseLoginPage/>, { wrapper: MemoryRouter });
    
        // Simulate user input and form submission
        fireEvent.change(screen.getByLabelText("Username:"), { target: { value: "Chikku" } });
        fireEvent.change(screen.getByLabelText("Password:"), { target: { value: "test123"}});

        // simulate form submission
        fireEvent.click(screen.getByRole("button", { name: /Log In/i }));

        await waitFor(() => {
            expect(screen.getByText("Unable to connect to the server. Please try again later.")).toBeInTheDocument();
        });
      });

  });
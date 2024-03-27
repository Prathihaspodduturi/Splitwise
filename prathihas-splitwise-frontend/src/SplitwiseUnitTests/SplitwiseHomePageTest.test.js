import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import SplitwiseHomePage from "../SplitWiseComponents/SplitwiseHomePage";
import { MemoryRouter } from "react-router-dom";


const mockedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'), // Preserve other exports
  useNavigate: () => mockedNavigate,
}));


// Mock global fetch and sessionStorage
// Before all tests, setup the mock sessionStorage
beforeAll(() => {
  global.fetch = jest.fn();
  /*fetch.mockResolvedValueOnce({
    ok: true,
    text: () => Promise.resolve('http://short.url/test'),
  });*/
  // Mock sessionStorage with jest.fn() for each method to use mockImplementation on them
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


// Clear mocks before each test
beforeEach(() => {
  fetch.mockClear();
  sessionStorage.clear();
});

describe("SplitwiseHomePage", () => {
  test("initially shows a loading state, then content", async () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve("Success"),
    });
    render(<SplitwiseHomePage />, { wrapper: MemoryRouter });

    expect(screen.getByText("Loading...")).toBeInTheDocument();
    
    await waitFor(() => {
      expect(screen.queryByText("Loading...")).not.toBeInTheDocument();
      expect(screen.getByText("Welcome to My Splitwise")).toBeInTheDocument();
    });
  });

  test("handles non-ok response from fetch", async () => {
    fetch.mockResolvedValueOnce({
      ok: false,
      text: () => Promise.resolve("Error message"), // You might adjust based on actual error handling
    });
    render(<SplitwiseHomePage />, { wrapper: MemoryRouter });
  
    await waitFor(() => {
      expect(screen.getByText("Unable to connect to server. Please Try again later!")).toBeInTheDocument();
    });
  });

  /*test("handles non-ok response from fetch 2", async () => {
    sessionStorage.getItem.mockImplementation((key) => {
      if (key === "token") return "dummy-token";
    });

    // initial successfull fetch simulation
    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve("Success"),
    });
  
    // Mock the second fetch call to simulate a failure that should trigger the error message
    fetch.mockRejectedValueOnce(new Error("Connection failure after token check"));

    render(<SplitwiseHomePage />, { wrapper: MemoryRouter });
    
    // Check for the expected error message
    await waitFor(() => {
      expect(screen.getByText("Unable to connect to server. Please Try again later!")).toBeInTheDocument();
    });
  });*/
  
  /*test("shows an error message if connection is terminated in middle", async () => {
    // initial successfull fetch simulation
    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve("Success"),
    });

    sessionStorage.getItem.mockImplementation((key) => {
      if (key === "token") return "dummy-token";
    });

    fetch.mockRejectedValueOnce(new Error("connection error"));

    render(<SplitwiseHomePage />, { wrapper: MemoryRouter });

    await waitFor(() => {
      const mockedNavigate = require('react-router-dom').useNavigate();
      expect(mockedNavigate).toHaveBeenCalledWith('/splitwise-home', expect.anything());
    });
  });*/

  test("shows an error message on initial connection failure", async () => {
    fetch.mockRejectedValueOnce(new Error("Failed to fetch"));
    render(<SplitwiseHomePage />, { wrapper: MemoryRouter });

    await waitFor(() => {
      expect(screen.getByText("Unable to connect to server. Please Try again later!")).toBeInTheDocument();
    });
  });

  test("renders Login and Signup NavLinks when not logged in", async () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve("Success"),
    });
    render(<SplitwiseHomePage />, { wrapper: MemoryRouter });

    await waitFor(() => {
      const loginLink = screen.getByRole('link', { name: "Login" });
      expect(loginLink).toBeInTheDocument();
      expect(loginLink).toHaveAttribute('href', '/splitwise-login');

      const signUpLink = screen.getByRole('link', { name: "Sign Up" });
      expect(signUpLink).toBeInTheDocument();
      expect(signUpLink).toHaveAttribute('href', '/splitwise-signup');
    });
  });

  test("fetch failure is called after submit button click", async () => {
    
    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve("Success"),
    });

    sessionStorage.getItem.mockImplementation((key) => {
      if (key === "token") return "dummy-token";
      return null;
    });

    fetch.mockRejectedValueOnce(new Error("connection error"));

    render(<SplitwiseHomePage />, { wrapper: MemoryRouter });

    await waitFor(() => expect(screen.queryByText("Loading...")).not.toBeInTheDocument());

    fireEvent.change(screen.getByLabelText(/Enter The Original URL:/i), { target: { value: 'http://test.url' } });
    
    // Simulate the button click that triggers the second fetch call
    fireEvent.click(screen.getByRole('button', { name: /Submit/i }));

    await waitFor(() => {
      expect(screen.getByText("connection error")).toBeInTheDocument();
    })
    
  });


  test("handles user input and form submission successfully", async () => {
    //Mocking logged in state
    sessionStorage.getItem.mockImplementation((key) => {
      if (key === "token") return "dummy-token";
    });

    //Mock fetch for intial connection check
    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve("Success"),
    });

    // Mock fetch for form submisson
    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve('http://short.url/test'),
    });

    render(<SplitwiseHomePage />, { wrapper: MemoryRouter });

    // Wait for the component to finish loading initial state
    await waitFor(() => expect(screen.queryByText("Loading...")).not.toBeInTheDocument());

    // Simulate user input and form submission
    fireEvent.change(screen.getByLabelText("Enter The Original URL:"), { target: { value: "http://test.url" } });
    fireEvent.click(screen.getByRole("button", { name: /Submit/i }));

    // Check for the presence of the shortened URL

    const shortenedUrlText = await screen.findByText('http://short.url/test'); // Increase timeout if necessary
    expect(shortenedUrlText).toBeInTheDocument();
  });

  test('logs out user on logout click', async () => {
    //Mocking logged in state
    sessionStorage.getItem.mockImplementation((key) => {
      if (key === "token") return "dummy-token";
    });


    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve("Success"),
    });

    // Mock fetch for form submisson
    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve('http://short.url/test'),
    });

    render(<SplitwiseHomePage />, { wrapper: MemoryRouter });

    // Wait for any initial loading states or fetch calls
    await waitFor(() => expect(screen.queryByText('Loading...')).not.toBeInTheDocument());

    // Simulate a click on the logout link
    fireEvent.click(screen.getByText('Logout'));

    // Verify sessionStorage.removeItem was called correctly
    expect(sessionStorage.removeItem).toHaveBeenCalledWith('token');

  });


  test("displays logout and My URLs links when user is logged in", async () => {

    sessionStorage.getItem.mockImplementation((key) => {
      if (key === "token") return "dummy-token";
    });

    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve("Success"),
    });

    // Mock fetch for form submisson
    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve('http://short.url/test'),
    });

    render(<SplitwiseHomePage />, { wrapper: MemoryRouter });

    // Wait for the component to finish loading initial state
    await waitFor(() => expect(screen.queryByText("Loading...")).not.toBeInTheDocument());

    expect(screen.getByText("Logout")).toBeInTheDocument();
    expect(screen.getByText("My URLs")).toBeInTheDocument();


  });
});

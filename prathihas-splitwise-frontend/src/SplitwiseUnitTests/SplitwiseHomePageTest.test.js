import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import SplitwiseHomePage from "../SplitWiseComponents/SplitwiseHomePage";

beforeAll(() => {
  global.fetch = jest.fn();
});

beforeEach(() => {
  fetch.mockClear();
});

describe("SplitwiseHomePage", () => {

  test("initially shows a loading state, then content", async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        text: () => Promise.resolve("Success"),
      })

      render(<SplitwiseHomePage/>, {wrapper: MemoryRouter});

      expect(screen.getByText("Loading...")).toBeInTheDocument();
    
    await waitFor(() => {
      expect(screen.queryByText("Loading...")).not.toBeInTheDocument();
      expect(screen.getByText("Welcome to Splitwise")).toBeInTheDocument();
      expect(screen.getByText("A simple site to split and maintain expenses")).toBeInTheDocument();
    });
  });

  test("renders Login and Signup NavLinks when not logged in", async () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      text: () => Promise.resolve("Success"),
    });
    render(<SplitwiseHomePage/>, { wrapper: MemoryRouter });

    await waitFor(() => {
      const loginLink = screen.getByRole('link', { name: "Login" });
      expect(loginLink).toBeInTheDocument();
      expect(loginLink).toHaveAttribute('href', '/splitwise/login');

      const signUpLink = screen.getByRole('link', { name: "Sign Up" });
      expect(signUpLink).toBeInTheDocument();
      expect(signUpLink).toHaveAttribute('href', '/splitwise/signup');
    });
  });

  test("handles non-ok response from fetch", async () => {
    fetch.mockResolvedValueOnce({
      ok: false,
      text: () => Promise.resolve("Error message"), // You might adjust based on actual error handling
    });
    render(<SplitwiseHomePage/>, { wrapper: MemoryRouter });
  
    await waitFor(() => {
      expect(screen.getByText("Unable to connect to the server. Please try again later.")).toBeInTheDocument();
      expect(screen.queryByText("Loading...")).not.toBeInTheDocument();
    });
  });

  test("shows an error message on initial connection failure", async () => {
    fetch.mockRejectedValueOnce(new Error("Failed to fetch"));
    render(<SplitwiseHomePage/>, { wrapper: MemoryRouter });

    await waitFor(() => {
      expect(screen.getByText("Unable to connect to the server. Please try again later.")).toBeInTheDocument();
    });
  });

});

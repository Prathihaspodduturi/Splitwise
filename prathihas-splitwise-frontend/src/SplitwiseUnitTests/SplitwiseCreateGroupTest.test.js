import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import SplitwiseCreateGroup from '../SplitWiseComponents/SplitwiseCreateGroup';
import { ToastContainer } from 'react-toastify';


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
        writable: true,
    });
});

beforeEach(() => {
    sessionStorage.clear();
    fetch.mockClear();
    mockNavigate.mockClear();
});

describe('Testing SplitwiseCreateGroup', () => {
    
    test('renders the create group form', () => {
        render(
            <BrowserRouter>
                <SplitwiseCreateGroup />
                <ToastContainer/>
            </BrowserRouter>
        );

        expect(screen.getByLabelText(/Group Name:/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Group Description:/i)).toBeInTheDocument();
        expect(screen.getByText(/Submit/i)).toBeInTheDocument();
        expect(screen.getByText(/Cancel/i)).toBeInTheDocument();
    });

    test('handles input changes', () => {
        render(
            <BrowserRouter>
                <SplitwiseCreateGroup />
                <ToastContainer/>
            </BrowserRouter>
        );

        const groupNameInput = screen.getByLabelText(/Group Name:/i);
        const groupDescriptionInput = screen.getByLabelText(/Group Description:/i);

        fireEvent.change(groupNameInput, { target: { value: 'Test Group' } });
        fireEvent.change(groupDescriptionInput, { target: { value: 'This is a test group.' } });

        expect(groupNameInput.value).toBe('Test Group');
        expect(groupDescriptionInput.value).toBe('This is a test group.');
    });

    test('submits the form successfully', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({}),
        });

        render(
            <BrowserRouter>
                <SplitwiseCreateGroup />
                <ToastContainer/>
            </BrowserRouter>
        );

        const groupNameInput = screen.getByLabelText(/Group Name:/i);
        const groupDescriptionInput = screen.getByLabelText(/Group Description:/i);
        const submitButton = screen.getByText(/Submit/i);

        fireEvent.change(groupNameInput, { target: { value: 'Test Group' } });
        fireEvent.change(groupDescriptionInput, { target: { value: 'This is a test group.' } });
        fireEvent.click(submitButton);

        await waitFor(() => {
            expect(screen.getByText(/Group Test Group created successfully/i)).toBeInTheDocument();
        });

        await act(() => new Promise((resolve) => setTimeout(resolve, 1000)));

        expect(mockNavigate).toHaveBeenCalledWith('/splitwise/groups');
    });

    test('cancels the form submission', async () => {
        render(
            <BrowserRouter>
                <SplitwiseCreateGroup />
                <ToastContainer/>
            </BrowserRouter>
        );

        const cancelButton = screen.getByText(/Cancel/i);
        fireEvent.click(cancelButton);

        await waitFor(() => {
            expect(mockNavigate).toHaveBeenCalledWith('/splitwise/groups');
        });
    });

    test('handles server error on form submission', async () => {
        fetch.mockResolvedValueOnce({
            ok: false,
            text: async () => "Server error",
        });

        render(
            <BrowserRouter>
                <SplitwiseCreateGroup />
                <ToastContainer/>
            </BrowserRouter>
        );

        const groupNameInput = screen.getByLabelText(/Group Name:/i);
        const groupDescriptionInput = screen.getByLabelText(/Group Description:/i);
        const submitButton = screen.getByText(/Submit/i);

        fireEvent.change(groupNameInput, { target: { value: 'Test Group' } });
        fireEvent.change(groupDescriptionInput, { target: { value: 'This is a test group.' } });

        fireEvent.click(submitButton);

        await waitFor(() => {
            expect(screen.getByText(/Failed to create the group/i)).toBeInTheDocument();
        });
    });

    test('handles connection error on form submission', async () => {
        fetch.mockRejectedValueOnce(new TypeError('Network error'));

        render(
            <BrowserRouter>
                <SplitwiseCreateGroup />
                <ToastContainer/>
            </BrowserRouter>
        );

        const groupNameInput = screen.getByLabelText(/Group Name:/i);
        const groupDescriptionInput = screen.getByLabelText(/Group Description:/i);
        const submitButton = screen.getByText(/Submit/i);

        fireEvent.change(groupNameInput, { target: { value: 'Test Group' } });
        fireEvent.change(groupDescriptionInput, { target: { value: 'This is a test group.' } });

        fireEvent.click(submitButton);

        await waitFor(() => {
            expect(screen.getByText(/Unable to connect to the server. Please try again later./i)).toBeInTheDocument();
        });
    });

});
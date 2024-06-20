import React from "react";
import { render, screen, fireEvent, waitFor,act } from "@testing-library/react";
import { BrowserRouter, MemoryRouter } from "react-router-dom";
import SplitwiseGroupsPage from '../SplitWiseComponents/SplitwiseGroupsPage';


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

describe('Testing SplitwiseGroupsPage', () => {
    test('renders loading state initially', async() => {
            
        
            render(
                <BrowserRouter>
                    <SplitwiseGroupsPage />
                </BrowserRouter>
            );

        await waitFor(() => {
            expect(screen.getByText("Loading...")).toBeInTheDocument();
        });
    });

    test('renders error message on fetch failure', async () => {
        fetch.mockResolvedValueOnce({
            ok: false,
            text: () => Promise.resolve("Fetch error"), // You might adjust based on actual error handling
          });

            render(
                <BrowserRouter>
                    <SplitwiseGroupsPage />
                </BrowserRouter>
            );

        await waitFor(() => {
            
            expect(screen.getByText("Fetch error")).toBeInTheDocument();
        });
    });

    test('renders connection error on network failure', async () => {
        fetch.mockRejectedValueOnce(new TypeError('Network error'));

        
            render(
                <BrowserRouter>
                    <SplitwiseGroupsPage />
                </BrowserRouter>
            );

        await waitFor(() => {
            expect(screen.getByText(/Unable to connect to the server. Please try again later./i)).toBeInTheDocument();
        });
    });

    test('renders groups fetched from API', async () => {
        const mockGroups = [
            { id: 1, groupName: 'Group 1', groupDescription: 'Description 1', settledUp: false, deleted: false, removedDate: null },
            { id: 2, groupName: 'Group 2', groupDescription: 'Description 2', settledUp: false, deleted: false, removedDate: null },
        ];
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockGroups,
        });

            render(
                <BrowserRouter>
                    <SplitwiseGroupsPage />
                </BrowserRouter>
            );

        await waitFor(() => {
            expect(screen.getByText(/Group 1/i)).toBeInTheDocument();
            expect(screen.getByText(/Description 1/i)).toBeInTheDocument();
            expect(screen.getByText(/Group 2/i)).toBeInTheDocument();
            expect(screen.getByText(/Description 2/i)).toBeInTheDocument();
        });
    });

    test('creates a new group', async () => {
        const mockGroups = [
            { id: 1, groupName: 'Group 1', groupDescription: 'Description 1', settledUp: false, deleted: false, removedDate: null },
        ];
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockGroups,
        });

        render(
            <BrowserRouter>
                <SplitwiseGroupsPage />
            </BrowserRouter>
        );

        await waitFor(() => {
            expect(screen.getByText(/Group 1/i)).toBeInTheDocument();
        });

        const createButton = screen.getByText(/Create Group/i);
        fireEvent.click(createButton);

        await waitFor(() => {
            // Check if navigation was called with the correct path
          expect(mockNavigate).toHaveBeenCalledWith('/splitwise/groups/creategroup');
        });
    });

    test('toggles settled groups section', async () => {
        const mockGroups = [
            { id: 1, groupName: 'Group 1', groupDescription: 'Description 1', settledUp: true, deleted: false, removedDate: null },
            { id: 2, groupName: 'Group 2', groupDescription: 'Description 2', settledUp: false, deleted: false, removedDate: null },
        ];
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockGroups,
        });

        render(
            <BrowserRouter>
                <SplitwiseGroupsPage />
            </BrowserRouter>
        );

        await waitFor(() => {
            expect(screen.getByText(/Group 2/i)).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText(/Show Settled Groups/i));

        await waitFor(() => {
            expect(screen.getByText(/Group 1/i)).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText(/Hide Settled Groups/i));

        await waitFor(() => {
            expect(screen.queryByText(/Group 1/i)).not.toBeInTheDocument();
        });
    });

    test('toggles deleted groups section', async () => {
        const mockGroups = [
            { id: 1, groupName: 'Group 1', groupDescription: 'Description 1', settledUp: false, deleted: true, removedDate: null },
            { id: 2, groupName: 'Group 2', groupDescription: 'Description 2', settledUp: false, deleted: false, removedDate: null },
        ];
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockGroups,
        });

        render(
            <BrowserRouter>
                <SplitwiseGroupsPage />
            </BrowserRouter>
        );

        await waitFor(() => {
            expect(screen.getByText(/Group 2/i)).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText(/Show Deleted Groups/i));

        await waitFor(() => {
            expect(screen.getByText(/Group 1/i)).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText(/Hide Deleted Groups/i));

        await waitFor(() => {
            expect(screen.queryByText(/Group 1/i)).not.toBeInTheDocument();
        });
    });

    test('restores a deleted group', async () => {
        const mockGroups = [
            { id: 1, groupName: 'Group 1', groupDescription: 'Description 1', settledUp: false, deleted: false, removedDate: null },
            { id: 2, groupName: 'Group 2', groupDescription: 'Description 2', settledUp: false, deleted: true, removedDate: null },
        ];
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockGroups,
        });

        render(
            <BrowserRouter>
                <SplitwiseGroupsPage />
            </BrowserRouter>
        );

        await waitFor(() => {
            expect(screen.getByText(/Group 1/i)).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText(/Show Deleted Groups/i));

        await waitFor(() => {
            expect(screen.getByText(/Group 2/i)).toBeInTheDocument();
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({}),
        });

        fireEvent.click(screen.getByText(/Restore/i));

        await waitFor(() => {
            expect(screen.queryByText(/Group 1/i)).toBeInTheDocument();
            expect(screen.queryByText(/Group 2/i)).toBeInTheDocument();
        });
    });

    test('renders error message on fetch failure', async () => {
        fetch.mockResolvedValueOnce({
            ok: false,
            text: () => Promise.resolve("Fetch error"), // You might adjust based on actual error handling
        });

        render(
            <BrowserRouter>
                <SplitwiseGroupsPage />
            </BrowserRouter>
        );

        await waitFor(() => {
            expect(screen.getByText("Fetch error")).toBeInTheDocument();
        });
    });

    test('renders connection error on network failure', async () => {
        fetch.mockRejectedValueOnce(new TypeError('Network error'));

        render(
            <BrowserRouter>
                <SplitwiseGroupsPage />
            </BrowserRouter>
        );

        await waitFor(() => {
            expect(screen.getByText(/Unable to connect to the server. Please try again later./i)).toBeInTheDocument();
        });
    });

});


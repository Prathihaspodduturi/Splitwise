import React from 'react';
import { render, screen, fireEvent, waitFor} from '@testing-library/react';
import { BrowserRouter,  MemoryRouter, Route, Routes } from 'react-router-dom';
import SplitwiseGroupDetail from '../SplitWiseComponents/SplitwiseGroupDetail';

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
    jest.useFakeTimers();
});

afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.useRealTimers();  // Reset to real timers
});

const mockDate = new Date();

const formatDate = (date) => {
    const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
    return new Date(date).toLocaleDateString('en-US', options);
  };



  const mockData = {
    detailedExpenses : [
        {
            amount : 1000, 
            dateCreated: new Date().toISOString(), 
            deleted: false, 
            expenseName: "miami", 
            id: 1, 
            involved : -333, 
            isPayment: false, 
            addedBy : "test"
        },
        {
            amount : 500, 
            dateCreated: new Date().toISOString(), 
            deleted: true, 
            expenseName: "tampa", 
            id: 2, 
            involved : 100, 
            isPayment: false, 
            addedBy : "test"
        }
    ],

    gmDetails: {
        addedBy: "test",
        addedDate: new Date().toISOString(),
        groupId: 1,
        removedBy: null,
        removedDate: null,
        username: "test"
    },

    group: {
        createdBy: "test",
        dateCreated: new Date().toISOString(),
        deleted: false,
        deletedBy: null,
        deletedDate: null,
        groupDescription: "testgroup",
        groupName: "testGroup",
        id: 1,
        settledBy: null,
        settledDate: null,
        settledUp: false
    },

    members:[
        {
            removedBy: null,
            removedDate: null,
            username: "test"
        },
        {
            removedBy: null,
            removedDate: null,
            username: "chikku"
        }
    ],

    transactions: [
        {
            amount: 33,
            fromUser: "test",
            toUser: "chikku"
        }
    ]
}

describe('Testing SplitwiseCreateGroup', () => {

    test('renders the group detail page', async () => {
        render(
            <BrowserRouter>
                <SplitwiseGroupDetail />
            </BrowserRouter>
        );
        
        await waitFor(() => {
            expect(screen.getByText("Loading...")).toBeInTheDocument();
        });
        
    });

    test('handles server error on form submission', async () => {
        fetch.mockResolvedValueOnce({
            ok: false,
            text: () => Promise.resolve("Failed to retrieve data"),
        });

        render(
            <BrowserRouter>
                <SplitwiseGroupDetail />
            </BrowserRouter>
        );

        await waitFor(() => {
            expect(screen.getByText("Failed to retrieve data")).toBeInTheDocument();
        });
    });

    test('handles connection error on form submission', async () => {
        fetch.mockRejectedValueOnce(new TypeError('Network error'));

        render(
            <BrowserRouter>
                <SplitwiseGroupDetail />
            </BrowserRouter>
        );

        await waitFor(() => {
            expect(screen.getByText(/try again later./i)).toBeInTheDocument();
        });
    });


    test('loading the data to be displayed', async() => {

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockData, 
        });

        render(
            <MemoryRouter initialEntries={['/splitwise/groups/1']}>
                    <Routes>
                        <Route path="/splitwise/groups/:groupId" element={<SplitwiseGroupDetail />} />
                    </Routes>
            </MemoryRouter>
        );

        await waitFor(() => {
            
            expect(screen.getByText("Logout")).toBeInTheDocument();
            expect(screen.getByText("testGroup")).toBeInTheDocument();
            expect(screen.getByText("Expenses")).toBeInTheDocument();
            expect(screen.getByText("Balances")).toBeInTheDocument();
            expect(screen.getByText("Deleted Expenses")).toBeInTheDocument();
            expect(screen.getByText("Members")).toBeInTheDocument();
            expect(screen.getByText("Total Expenses")).toBeInTheDocument();
            expect(screen.getByText("Add Expense")).toBeInTheDocument();
        });

    });

    test('adding member', async() => {

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockData, 
        });

        render(
            <MemoryRouter initialEntries={['/splitwise/groups/1']}>
                    <Routes>
                        <Route path="/splitwise/groups/:groupId" element={<SplitwiseGroupDetail />} />
                    </Routes>
            </MemoryRouter>
        );

        await waitFor(() => {
            expect(screen.getByText("Members")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Members"));

        await waitFor(() => {
            expect(screen.getByText("test")).toBeInTheDocument();
            expect(screen.getByText("chikku")).toBeInTheDocument();
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({}), 
        });

        fireEvent.click(screen.getByText("Add Member"));

        await waitFor(() => {
            expect(screen.getByPlaceholderText('Username')).toBeInTheDocument();
            expect(screen.getByText('Add')).toBeInTheDocument();
            expect(screen.getByText('Cancel')).toBeInTheDocument();
        });

        const input = screen.getByPlaceholderText('Username');
        fireEvent.change(input, { target: { value: 'newuser123' } });

        fireEvent.click(screen.getByText("Add"));

        await waitFor(() => {
            expect(screen.getByText('Member added successfully!')).toBeInTheDocument();
        });
    });


});
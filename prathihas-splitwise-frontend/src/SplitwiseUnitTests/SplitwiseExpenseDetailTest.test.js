import React from 'react';
import { render, screen, fireEvent, waitFor} from '@testing-library/react';
import { BrowserRouter, Route, Routes, MemoryRouter } from 'react-router-dom';
import SplitwiseExpenseDetailPage from '../SplitWiseComponents/SplitwiseExpenseDetailPage';

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

    addedBy: "test",
    amount : 50,
    dateCreated : new Date().toISOString(), 
    expenseName : "testing group",
    isPayment : false,
    lastUpdatedDate : new Date().toISOString(), 
    updatedBy: "test",
    gmRemovedDate : null,
    participants: [
        {
            amountPaid : 50,
            isChecked: true,
            username: "test",
            amountOwed: 25
        },
        {
            amountPaid : 0,
            isChecked: true,
            username: "chikku",
            amountOwed: 25
        }
    ]
} 

describe('Testing SplitwiseGroupsPage', () => {

    test('renders loading state initially', async() => {  
        render(
            <BrowserRouter>
                <SplitwiseExpenseDetailPage />
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
                    <SplitwiseExpenseDetailPage />
                </BrowserRouter>
            );

        await waitFor(() => {
            
            expect(screen.getByText(/Fetch error/)).toBeInTheDocument();
        });
    });

    test('renders connection error on network failure', async () => {
        fetch.mockRejectedValueOnce(new TypeError('Network error'));

        
            render(
                <BrowserRouter>
                    <SplitwiseExpenseDetailPage />
                </BrowserRouter>
            );

        await waitFor(() => {
            expect(screen.getByText(/Unable to connect to the server. Please try again later./i)).toBeInTheDocument();
        });
    });

    test('loading expense details and testing delete expense', async() => {

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockData, 
        });

        render(
            <MemoryRouter initialEntries={['/splitwise/groups/1/expenses/1']}>
                    <Routes>
                        <Route path="/splitwise/groups/:groupId/expenses/:expenseId" element={<SplitwiseExpenseDetailPage />} />
                    </Routes>
            </MemoryRouter>
        );

        await waitFor(() => {
            expect(screen.getByText("Expense Details")).toBeInTheDocument();
            expect(screen.getByText("testing group")).toBeInTheDocument();
            expect(screen.getByText("Amount: $50.00")).toBeInTheDocument();
            expect(screen.getByText(`Added by: test on ${formatDate(mockDate)}`)).toBeInTheDocument();
            expect(screen.getByText(`Last Updated By: test on ${formatDate(mockDate)}`)).toBeInTheDocument();

            expect(screen.getByText("Paid by:")).toBeInTheDocument();
            expect(screen.getByText("test: paid $50.00")).toBeInTheDocument();
            expect(screen.getByText("Participants:")).toBeInTheDocument();
            expect(screen.getByText("chikku: Owes $25.00")).toBeInTheDocument();
            expect(screen.getByText("test: Owes $25.00")).toBeInTheDocument();
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: {} 
        });

        fireEvent.click(screen.getByText("Delete Expense"));

        await waitFor(() => {
            expect(screen.getByText("Are you sure you want to delete this expense?")).toBeInTheDocument();
            expect(screen.getByText("Yes")).toBeInTheDocument();
            expect(screen.getByText("Cancel")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Cancel"));

        await waitFor(() => {
            expect(screen.getByText("Delete Expense")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Delete Expense"));

        await waitFor(() => {
            expect(screen.getByText("Are you sure you want to delete this expense?")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Yes"));

        await waitFor(() => {
            expect(screen.getByText("Deleted successfully!")).toBeInTheDocument();
        });

        jest.advanceTimersByTime(2000);

        await waitFor(() => {
            expect(mockNavigate).toHaveBeenCalledWith('/splitwise/groups/1');
        });

        fetch.mockResolvedValueOnce({
            ok: false,
            text: () => Promise.resolve("Fetch error"), 
        });

        fireEvent.click(screen.getByText("Delete Expense"));

        await waitFor(() => {
            expect(screen.getByText("Are you sure you want to delete this expense?")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Yes"));

        await waitFor(() => {
            expect(screen.getByText("Failed to delete")).toBeInTheDocument();
        });

        fetch.mockRejectedValueOnce(new TypeError('Network error'));

        fireEvent.click(screen.getByText("Delete Expense"));

        await waitFor(() => {
            expect(screen.getByText("Are you sure you want to delete this expense?")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Yes"));

        await waitFor(() => {
            expect(screen.getByText("Unable to connect to the server. Please try again later.")).toBeInTheDocument();
        });



    });

});

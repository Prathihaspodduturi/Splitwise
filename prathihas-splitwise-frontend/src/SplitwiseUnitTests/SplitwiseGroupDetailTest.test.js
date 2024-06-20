import React from 'react';
import { render, screen, fireEvent, waitFor} from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
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
});

describe('Testing SplitwiseCreateGroup', () => {

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
            expect(screen.getByText(/Unable to connect to the server. Please try again later./i)).toBeInTheDocument();
        });
    });


    // test('loading the data to be displayed', async() => {

    //     sessionStorage.getItem.mockImplementation((key) => {
    //         if (key === 'username') return 'test';
    //         return null;
    //     });

    //     fetch.mockResolvedValueOnce({
    //         ok: true,
    //         json: async () => mockData,
    //     });

    //     render(
    //         <BrowserRouter>
    //             <SplitwiseGroupDetail />
    //         </BrowserRouter>
    //     );

    //     await waitFor(() => {
            
    //         expect(screen.getByText("Logout")).toBeInTheDocument();
    //         expect(screen.getByText("testGroup")).toBeInTheDocument();
    //         expect(screen.getByText("Expenses")).toBeInTheDocument();
    //         expect(screen.getByText("Balances")).toBeInTheDocument();
    //         expect(screen.getByText("Deleted Expenses")).toBeInTheDocument();
    //         expect(screen.getByText("Members")).toBeInTheDocument();
    //         expect(screen.getByText("Total Expenses")).toBeInTheDocument();

    //         expect(screen.getByText("Add Expense")).toBeInTheDocument();
    //         expect(screen.getByText("miami")).toBeInTheDocument();
    //         expect(screen.getByText("$1000.00 - Date: 6/5/2024")).toBeInTheDocument();
    //         expect(screen.getByText("You owe $333.00")).toBeInTheDocument();
    //     });

    //     await waitFor(() => {
    //         fireEvent.click(screen.getByText("Add Expense"));
    //     });

    //     await waitFor(() => {
    //         expect(screen.getByPlaceholderText("Expense Name")).toBeInTheDocument();
    //         expect(screen.getByPlaceholderText("Total Amount")).toBeInTheDocument();
    //         expect(screen.getByText("Add Payers")).toBeInTheDocument();
    //         expect(screen.getByText("Add Participants")).toBeInTheDocument();
    //         expect(screen.getByText("Done")).toBeInTheDocument();
    //         expect(screen.getByText("Cancel")).toBeInTheDocument();
    //     });

    //     fireEvent.change(screen.getByPlaceholderText("Expense Name"), { target: { value: 'Test Expense' } });
    //     fireEvent.change(screen.getByPlaceholderText("Total Amount"), { target: { value: '100' } });

    //     expect(screen.getByPlaceholderText("Expense Name").value).toBe('Test Expense');
    //     expect(screen.getByPlaceholderText("Total Amount").value).toBe('100');

    //     fireEvent.click(screen.getByText("Add Payers"));

    //     await waitFor(() => {
    //         expect(screen.getByLabelText("test")).toBeInTheDocument();
    //         expect(screen.getByLabelText("chikku")).toBeInTheDocument();
    //     });

    //     fireEvent.change(screen.getByLabelText("test"), { target: { value: '60' } });
    //     fireEvent.change(screen.getByLabelText("chikku"), { target: { value: '40' } });

    //     expect(screen.getByLabelText("test").value).toBe('60');
    //     expect(screen.getByLabelText("chikku").value).toBe('40');

    //     fireEvent.click(screen.getByText("Close"));

    //     fireEvent.click(screen.getByText("Add Participants"));

    //     await waitFor(() => {
    //         expect(screen.getByLabelText("test")).toBeInTheDocument();
    //         expect(screen.getByLabelText("chikku")).toBeInTheDocument();
    //     });

    //     fireEvent.click(screen.getByLabelText("test"));
    //     fireEvent.click(screen.getByLabelText("chikku"));

    //     // Verify the checkboxes are checked
    //     expect(screen.getByLabelText("test").checked).toBe(true);
    //     expect(screen.getByLabelText("chikku").checked).toBe(true);

    //     // Simulate closing participants section
    //     fireEvent.click(screen.getByText("Close"));

    //     fetch.mockResolvedValueOnce({
    //         ok: true,
    //         json: async () => ({ groupId: 3, amount: 100, expenseName: 'Test Expense', involved: -50, isPayment: false })
    //     });


    //     // fireEvent.click(screen.getByText("Done"));

    //     // await waitFor(() => {
    //     //     expect(screen.getByText("Expense added successfully!")).toBeInTheDocument();
    //     // });

    //     // fireEvent.click(screen.getByText("Balances"));

    //     // await waitFor(() => {
    //     //     expect(screen.getByText("test owes $33 to chikku")).toBeInTheDocument();
    //     //     expect(screen.getByText("Pay")).toBeInTheDocument();
    //     // });



    //     // const deletedExpensesDiv = screen.getByText("Deleted Expenses");
    //     // fireEvent.click(deletedExpensesDiv);

    //     // await waitFor(() => {
    //     //     expect(screen.getByText("tampa")).toBeInTheDocument();
    //     //     //expect(screen.getByText(" - $500.00")).toBeInTheDocument();
    //     //     expect(screen.getByText("You get back $100.00")).toBeInTheDocument();
    //     // });

    //     // const membersDiv = screen.getByText("Members");
    //     // fireEvent.click(membersDiv);

    //     // await waitFor(() => {
    //     //     expect(screen.getByText("test")).toBeInTheDocument();
    //     //     expect(screen.getByText("Leave Group")).toBeInTheDocument();
    //     //     expect(screen.getByText("chikku")).toBeInTheDocument();
    //     //     expect(screen.getByText("Remove")).toBeInTheDocument();
    //     // });

    // });

});
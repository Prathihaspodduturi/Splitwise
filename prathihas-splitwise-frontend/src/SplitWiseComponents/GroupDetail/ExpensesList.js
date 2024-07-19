import React, {useState, useEffect, useCallback} from 'react';
import styles from './ExpensesList.module.css'
import SplitwiseExpenseDetailPage from '../ExpenseDetail/SplitwiseExpenseDetailPage';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

const ExpensesList = ({ groupId, whichExpenses, action, handleAction, setEverythingToNull, refreshExpenses, deletedBy, settledBy}) => {

    const navigate = useNavigate();
    const [activeExpenses, setActiveExpenses] = useState([]);
    const [deletedExpenses, setDeletedExpenses] = useState([]);

    const [connectionError, setConnectionError] = useState(null);

    const fetchExpenses = async () => {
        try {
            const response = await fetch(`http://52.15.44.104:8080/splitwise/groups/${groupId}/expenses`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
            });

            if (!response.ok) {
                const data = await response.text();
                throw new Error(data);
            }

            const data = await response.json();
 

            const filteredExpenses = data.detailedExpenses.filter(exp => {
                const expDate = new Date(exp.dateCreated);
                const addedDate = new Date(data.gmDetails.addedDate);
                const removedDate = data.gmDetails.removedDate ? new Date(data.gmDetails.removedDate) : new Date();
            
                const updatedDate = exp.lastUpdatedDate ? new Date(exp.lastUpdatedDate) : null;
                const deletedDate = exp.deletedDate ? new Date(exp.deletedDate) : null;
            
                const isExpDateValid = expDate >= addedDate && expDate <= removedDate;
                const isUpdatedDateValid = updatedDate ? (updatedDate >= addedDate && updatedDate <= removedDate) : false;
                const isDeletedDateValid = deletedDate ? (deletedDate >= addedDate && deletedDate <= removedDate) : false;
            
                return isExpDateValid || isUpdatedDateValid || isDeletedDateValid;
            });

            const sortedExpenses = filteredExpenses.sort((a, b) => -(new Date(a.dateCreated) - new Date(b.dateCreated)));
            setActiveExpenses(sortedExpenses.filter(exp => !exp.deleted));
            setDeletedExpenses(sortedExpenses.filter(exp => exp.deleted));
            
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
                setTimeout(() => navigate('/prathihas-splitwise/logout'), 3000);
            } else {
                toast.error("failed to retrieve");
            }
        }
    };

    useEffect(() => {
        fetchExpenses();
    }, [groupId, refreshExpenses]);


    const [selectedExpenseId, setSelectedExpenseId] = useState(null);

    const handleExpenseClick = (expenseId) => {
        setSelectedExpenseId(selectedExpenseId === expenseId ? null : expenseId);
    };

    if(connectionError)
    {
        return <div>{connectionError}</div>
    }

    const stopPropagation = (e) => {
        e.stopPropagation();
    };

    const expensesToShow = whichExpenses ? activeExpenses : deletedExpenses;

    return (


        <div className={styles.expensesSection}>
        <h1>{whichExpenses ? 'active' : 'deleted'} expenses</h1>
        {expensesToShow.length > 0 ? (
            <ul className={styles.expensesList}>
                {expensesToShow.map((expense, index) => (
                    <li key={index} className={styles.expenseItem} onClick={() => handleExpenseClick(expense.id)}>
                        <strong>
                            <div className={styles.expenseName}>
                                {expense.expenseName} {expense.isPayment && "(Payment)"}
                            </div>
                        </strong>
                        <div className={styles.expenseItemDetails}>
                            ${expense.amount.toFixed(2)} - Date: {new Date(expense.dateCreated).toLocaleDateString()}
                            {expense.notInvolved ? (
                                <p className={styles.expenseNeutral}>Not involved</p>
                            ) : (
                                <p className={expense.involved >= 0 ? styles.expenseGetBack : styles.expenseOwe}>
                                    You {expense.involved >= 0 ? 'get back' : 'owe'} ${Math.abs(expense.involved).toFixed(2)}
                                </p>
                            )}
                        </div>
                        {selectedExpenseId === expense.id && (
                            <div className={styles.expenseDetails} onClick={stopPropagation}>
                                <SplitwiseExpenseDetailPage 
                                    groupId={groupId} 
                                    expenseId={expense.id} 
                                    fetchExpenses={fetchExpenses} 
                                    action={action}
                                    handleAction={handleAction}
                                    setEverythingToNull={setEverythingToNull}
                                    deletedBy={deletedBy}
                                    settledBy={settledBy}
                                />
                            </div>
                        )}
                    </li>
                ))}
            </ul>
        ) : (
            <p>No {whichExpenses ? 'active' : 'deleted'} expenses recorded.</p>
        )}
    </div>

    );
};

export default ExpensesList;

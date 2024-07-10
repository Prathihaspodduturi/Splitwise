import React from 'react';
import { NavLink } from 'react-router-dom';
import styles from './ExpensesList.module.css'

const ExpensesList = ({ group, expenses, activeExpenses, groupId, gmDetails, toggleAddExpense}) => {
    return (

        <div className={styles.expensesSection}>
                        {(!group.settledUp && gmDetails.removedDate === null)&& <button onClick={toggleAddExpense} className={styles.addExpenseButton}>
                            Add Expense
                        </button> }
                        {expenses.length > 0 ? (
                            <ul className={styles.expensesList}>
                                {activeExpenses.map((expense, index) => (
                                    <li key={index} className={styles.expenseItem}>
                                    {expense.isPayment ? (
                                        <div className={styles.paymentItem}>
                                            <strong>
                                                <NavLink to={`/splitwise/groups/${groupId}/expenses/${expense.id}`} className={styles.paymentDetails}>
                                                    {expense.expenseName} (Payment)
                                                </NavLink>
                                            </strong>
                                            <div className={styles.paymentDetails}>
                                                Paid: ${expense.amount.toFixed(2)} -
                                                Date: {new Date(expense.dateCreated).toLocaleDateString()}
                                            </div>
                                        </div>
                                    ) :
                                    (
                                    <>
                                        <strong>
                                        <NavLink to={`/splitwise/groups/${groupId}/expenses/${expense.id}`}>
                                            {expense.expenseName}
                                        </NavLink>
                                    </strong>
                                    <div className={styles.expenseItemDetails}>
                                        ${expense.amount.toFixed(2)} - 
                                        Date: {new Date(expense.dateCreated).toLocaleDateString()}
                                        {expense.notInvolved ?
                                            (<p className={styles.expenseNeutral}>Not involved</p>)
                                            :
                                            (<p className={expense.involved >=0 ? styles.expenseGetBack : styles.expenseOwe}>
                                                You {expense.involved >= 0 ? 'get back' : 'owe'} ${Math.abs(expense.involved).toFixed(2)}
                                            </p>)
                                        }
                                    </div>
                                    </>
                                    )}
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>No active expenses recorded.</p>
                        )}
                    </div>

    );
};

export default ExpensesList;

import React from 'react';
import styles from './SplitwiseExpenseDetailPage.module.css'; // Assuming styles are correctly defined in this CSS module

const ExpenseHeader = ({ expense }) => {
  if (!expense) return null; // Renders nothing if no expense data is available

  return (
    <div>
      <h2 className={styles.expenseName}>{expense.expenseName}</h2>
      {expense.amount && <p className={styles.expenseAmount}>Amount: ${expense.amount.toFixed(2)}</p>}
      <p className={styles.addedBy}>Added by: {expense.addedBy} on {new Date(expense.dateCreated).toLocaleDateString()}</p>
      {expense.updatedBy && (
        <p className={styles.updatedBy}>Last Updated By: {expense.updatedBy} on {new Date(expense.lastUpdatedDate).toLocaleDateString()}</p>
      )}
      {expense.deletedBy && (
        <p className={styles.deletedBy}>Deleted By: {expense.deletedBy} on {new Date(expense.deletedDate).toLocaleDateString()}</p>
      )}
    </div>
  );
};

export default ExpenseHeader;

import React from 'react';
import styles from './SplitwiseExpenseDetailPage.module.css'; // Assuming styles are correctly defined in this CSS module

const ExpenseHeader = ({ expense }) => {
  if (!expense) return null; // Renders nothing if no expense data is available

  return (
    <div>
      {expense.amount && <p className={styles.expenseAmount}>Amount: ${expense.amount.toFixed(2)}</p>}
      <p className={styles.commonText}>Added by: {expense.addedBy} on {new Date(expense.dateCreated).toLocaleDateString()}</p>
      {expense.updatedBy && (
        <p className={styles.commonText}>Last Updated By: {expense.updatedBy} on {new Date(expense.lastUpdatedDate).toLocaleDateString()}</p>
      )}
      {expense.deletedBy && (
        <p className={styles.commonText}>Deleted By: {expense.deletedBy} on {new Date(expense.deletedDate).toLocaleDateString()}</p>
      )}
    </div>
  );
};

export default ExpenseHeader;

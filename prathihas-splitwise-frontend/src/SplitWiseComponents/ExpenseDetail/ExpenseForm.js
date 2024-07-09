import React from 'react';
import styles from './SplitwiseExpenseDetailPage.module.css';

const ExpensePaymentForm = ({ editExpense, handleInputChange, handleParticipantAmountChange, handleParticipantCheckboxChange, totalMismatch, handleUpdate, setEditMode }) => {
    return (
        <div className={styles.formContainer}>
            <div className={styles.formRow}>
                <label htmlFor="expenseName">Expense Name:</label>
                <input
                    id="expenseName"
                    type="text"
                    value={editExpense.expenseName}
                    onChange={e => handleInputChange('expenseName', e.target.value)}
                />
            <div/>
            <label htmlFor="amount">Amount:</label>
            <input
                id="amount"
                type="number"
                value={editExpense.amount}
                onChange={e => handleInputChange('amount', e.target.value)}
            />
            <div>
                <h3>Payers</h3>
                {editExpense.participants.map(participant => (
                    <div key={participant.username}>
                        <label htmlFor={"amountPaid" + participant.username}>{participant.username}:</label>
                        <input
                            id={"amountPaid" + participant.username}
                            type="number"
                            value={participant.amountPaid}
                            onChange={e => handleParticipantAmountChange(participant.username, 'amountPaid', e.target.value)}
                        />
                    </div>
                ))}
            </div>
            <div>
                <h3>Participants</h3>
                {editExpense.participants.map(participant => (
                    <div key={participant.username}>
                        <label>
                            <input
                                type="checkbox"
                                checked={participant.isChecked}
                                onChange={() => handleParticipantCheckboxChange(participant.username)}
                            />
                            {participant.username}
                        </label>
                    </div>
                ))}
            </div>
            </div>
        </div>
    );
};

export default ExpensePaymentForm;

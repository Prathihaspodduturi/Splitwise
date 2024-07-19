import React from 'react';
import styles from './SplitwiseExpenseDetailPage.module.css';

const ExpenseForm = ({ editExpense, handleInputChange, handleParticipantAmountChange, handleParticipantCheckboxChange}) => {
    return (
        <div className={styles.formContainer}>
            <div className={styles.formRow}>
                <label htmlFor="expenseName" className={styles.labelForm}>Expense Name:</label>
                <input
                    id="expenseName"
                    type="text"
                    value={editExpense.expenseName}
                    className={styles.inputForm}
                    onChange={e => handleInputChange('expenseName', e.target.value)}
                />
            <div/>
            <div className={styles.formRow}>
                <label htmlFor="amount" className={styles.labelForm}>Amount:</label>
                <input
                    id="amount"
                    type="number"
                    className={styles.inputForm}
                    value={editExpense.amount}
                    onChange={e => handleInputChange('amount', e.target.value)}
                />
            </div>
            <div className={styles.participantsContainer}>
                <div className={styles.partcipantsSection}>
                    <h3 className={styles.labelForm}>Payers</h3>
                    {editExpense.participants.map(participant => (
                        <div key={participant.username}>
                            <label htmlFor={"amountPaid" + participant.username} className={styles.labelForm}>{participant.username}:</label>
                            <input
                                id={"amountPaid" + participant.username}
                                type="number"
                                value={participant.amountPaid}
                                className={styles.inputForm}
                                onChange={e => handleParticipantAmountChange(participant.username, 'amountPaid', e.target.value)}
                            />
                        </div>
                    ))}
                </div>
                <div className={styles.participantsSection}>
                    <h3 className={styles.labelForm}>Participants</h3>
                    {editExpense.participants.map(participant => (
                        <div key={participant.username}>
                            <label>
                                <input
                                    type="checkbox"
                                    checked={participant.isChecked}
                                    className={styles.userNameForm}
                                    onChange={() => handleParticipantCheckboxChange(participant.username)}
                                />
                                {participant.username}
                            </label>
                        </div>
                    ))}
                </div>
            </div>
            </div>
        </div>
    );
};

export default ExpenseForm;

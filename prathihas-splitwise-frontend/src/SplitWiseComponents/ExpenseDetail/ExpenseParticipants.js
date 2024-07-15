import React from 'react';
import styles from './SplitwiseExpenseDetailPage.module.css'

const ExpenseParticipants = ({ participants }) => {
    if (!participants || participants.length === 0) return null;

    return (
        <div className={styles.participantsContainer}>
        <div className={styles.participantsSection}>
            {participants && (
                <>
                    <h3>Paid by:</h3>
                    <ul>
                        {participants.filter(participant => participant.amountPaid > 0).map(participant => (
                            <li key={participant.username}>
                                {participant.username} paid ${participant.amountPaid.toFixed(2)}
                            </li>
                        ))}
                    </ul>
                </>
            )}
        </div>
        <div className={styles.participantsSection}>
            {participants && (
                <>
                    <h3>Participants:</h3>
                    <ul>
                        {participants.filter(participant => participant.amountOwed > 0).map(participant => (
                            <li key={participant.username}>
                                {participant.username} Owes ${participant.amountOwed.toFixed(2)}
                            </li>
                        ))}
                    </ul>
                </>
            )}
        </div>
    </div>
    );
};

export default ExpenseParticipants;

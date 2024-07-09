import React from 'react';

const ExpenseParticipants = ({ participants }) => {
    if (!participants || participants.length === 0) return null;

    return (
        <div>
           {
                participants && (
                    <>
                        <h3>Paid by:</h3>
                        <ul>
                            {participants.filter(participant => participant.amountPaid > 0).map(participant => (
                                (<li key={participant.username}>
                                    {participant.username}: paid ${participant.amountPaid.toFixed(2)}
                                </li>)
                            ))}
                        </ul>
                    </>
                )
            }
            {
                participants && (
                    <>
                        <h3>Participants:</h3>
                        <ul>
                            {participants.filter(participant => participant.amountOwed > 0).map(participant => (
                                (<li key={participant.username}>
                                    {participant.username}: Owes ${participant.amountOwed.toFixed(2)}
                                </li>)
                            ))}
                        </ul>
                    </>
                )
            }
        </div>
    );
};

export default ExpenseParticipants;

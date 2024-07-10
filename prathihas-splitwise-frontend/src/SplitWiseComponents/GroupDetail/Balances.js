// File: SplitWiseComponents/Balances.js

import React from 'react';
import styles from './Balances.module.css';
import stylesHead from './SplitwiseGroupDetail.module.css';

const Balances = ({ balances, gmDetails, openPaymentModal}) => {

    return (
        <div>
            <div className={styles.listBalancesContainer}>
            <h3 className={styles.balancesHeading}>All Balances</h3>
            {balances.length > 0 ? (
                <ul>
                    {balances.map((balance, index) => (
                        <li key={index} className={styles.listBalancesItem}>
                            <p>{balance.fromUser} owes ${balance.amount} to {balance.toUser}</p>
                            {gmDetails.removedDate === null && <button onClick={() => openPaymentModal(balance)} className={styles.balancesPayButton}>Pay</button>}
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No outstanding balances.</p>
            )}
        </div>
        </div>
        
    );
};

export default Balances;

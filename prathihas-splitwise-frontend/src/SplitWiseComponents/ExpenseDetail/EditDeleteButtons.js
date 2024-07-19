// EditDeleteButtons.js
import React from 'react';
import styles from './SplitwiseExpenseDetailPage.module.css';


const EditDeleteButtons = ({
    isDeleted,
    gmRemovedDate,
    setEditMode,
    isPayment,
    handleAction,
    deletedBy, 
    settledBy
}) => { 

    
    if(deletedBy !== null || settledBy !== null)
            return;

    let editLabel = "Edit Expense";
    let deleteLabel = "Delete Expense";
    let restoreLabel = "Restore Expense";

    if(isPayment)
    {
        editLabel = "Edit Payment";
        deleteLabel = "Delete Payment";
        restoreLabel = "Restore Payment";
    }

    

    const handleDeleteMethod = () => {
        const message = `Are you sure you want to delete this ${isPayment ? 'Payment' : 'Expense'}?`
        handleAction('delete', message);
    }

    const handleRestoreMethod = () => {
        const message = `Are you sure you want to restore this ${isPayment ? 'Payment' : 'Expense'}?`
        handleAction('restore', message);
    }

    return (
        <div className={styles.buttonContainer}>
            {!isDeleted ? (
                <>
                    {gmRemovedDate === null && (
                        <button className={styles.editButton} onClick={() => setEditMode(true)}>
                            {editLabel}
                        </button>
                    )}
                    {gmRemovedDate === null && (
                        <button 
                            className={styles.deleteButton} 
                            onClick={handleDeleteMethod}
                        >
                            {deleteLabel}
                        </button>
                    )}
                </>
            ) : (
                <>
                    {gmRemovedDate === null && (
                        <button 
                            className={styles.restoreButton} 
                            onClick={handleRestoreMethod}
                        >
                            {restoreLabel}
                        </button>
                    )}
                </>
            )}
        </div>
    );
};

export default EditDeleteButtons;

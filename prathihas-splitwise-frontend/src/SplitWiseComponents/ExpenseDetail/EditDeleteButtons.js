// EditDeleteButtons.js
import React from 'react';
import styles from './SplitwiseExpenseDetailPage.module.css';


const EditDeleteButtons = ({
    isDeleted,
    gmRemovedDate,
    setEditMode,
    setShowConfirmModal,
    setModalAction,
    isPayment
}) => { 

    //console.log("editDleteButtons isPayment"+isPayment);

    let editLabel = "Edit Expense";
    let deleteLabel = "Delete Expense";
    let restoreLabel = "Restore Expense";

    if(isPayment)
    {
        editLabel = "Edit Payment";
        deleteLabel = "Delete Payment";
        restoreLabel = "Restore Payment";
    }

    const handleActionInitiation = (action) => {
        setModalAction(action);
        setShowConfirmModal(true);
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
                            onClick={() => {
                                handleActionInitiation('delete')
                            }}
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
                            onClick={() => {
                                handleActionInitiation('restore')
                            }}
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

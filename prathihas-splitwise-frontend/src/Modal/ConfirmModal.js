// File: ConfirmModal.js
import React from 'react';
import styles from './ConfirmModal.module.css'; // Assumed CSS module for styling

const ConfirmModal = ({ isOpen, onClose, onConfirm, message }) => {
    if (!isOpen) return null;

    const handleOverlayClick = (e) => {
        onClose();
    };

    const handleModalClick = (e) => {
        e.stopPropagation();
    };

    return (
        <div className={styles.modalOverlay} onClick={handleOverlayClick}>
            <div className={styles.modalContent} onClick={handleModalClick}>
                <p>{message}</p>
                <button onClick={onConfirm} className={styles.confirmButton}>Yes</button>
                <button onClick={onClose} className={styles.cancelButton}>Cancel</button>
            </div>
        </div>
    );
};

export default ConfirmModal;

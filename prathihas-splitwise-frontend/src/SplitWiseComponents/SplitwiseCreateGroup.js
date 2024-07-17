import React, { useState, useEffect } from "react";
import { NavLink, useNavigate, Link } from 'react-router-dom';
import styles from './SplitwiseCreateGroup.module.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';


const SplitwiseCreateGroup = ({setShowCreateGroupForm, closeConfirmModal, setGroups}) => {
    const navigate = useNavigate();

    const [groupName, setGroupName] = useState('');
    const [groupDescription, setGroupDescription] = useState('');
    const [error, setError] = useState('');
    const [connectionError, setConnectionError] = useState('');

    const handleCreateGroup = async (event) => {
        event.preventDefault();
        setError('');
        setConnectionError('');
        const token = sessionStorage.getItem('token');
        try {
            const response = await fetch("http://localhost:8080/splitwise/creategroup", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ groupName, groupDescription })
            });

            if (!response.ok) {
                const data = await response.text();
                throw new Error(data);
            }

            const data = await response.text();

            toast.success(`Group ${groupName} created successfully`);

            closeConfirmModal();

            setTimeout(() => {
                setGroups(prev => [... prev, ...data]);
            }, 2000);

        } catch (error) {
            closeConfirmModal();
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                toast.error('Failed to create the group');
            }
        }
    };

    if (connectionError) {
        return (
          <div className={styles.errorMessage}>{connectionError}</div>
        );
    }

    return (
        <div className={styles.modalOverlay} onClick={() => setShowCreateGroupForm(false)}>
            <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <form onSubmit={handleCreateGroup} className={styles.form}>
                <div>
                    <label htmlFor="groupName" className={styles.formLabel}>Group Name:</label>
                    <input
                        id="groupName"
                        type="text"
                        value={groupName}
                        onChange={(e) => setGroupName(e.target.value)}  required
                        className={styles.formInput}
                    />
                </div>
                <div>
                    <label htmlFor="groupDescription" className={styles.formLabel}>Group Description:</label>
                    <textarea
                        id="groupDescription"
                        type="text"
                        value={groupDescription}
                        onChange={(e) => setGroupDescription(e.target.value)}  required
                        className={styles.formTextArea}
                    />
                </div>
                <button type="submit" className={styles.button}>Submit</button>
                <button type="button" onClick={() => setShowCreateGroupForm(false)} className={styles.buttonCancel}>Cancel</button>
            </form>
            <ToastContainer position="top-center" autoClose={2000} hideProgressBar={false} newestOnTop={false} closeOnClick rtl={false} pauseOnFocusLoss={false} draggable={true} pauseOnHover={true} />
        </div>
        </div>
    );

}

export default SplitwiseCreateGroup;
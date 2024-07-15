import React, { useEffect, useState, useCallback } from 'react';
import styles from './SplitwiseExpenseDetailPage.module.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ExpenseHeader from './ExpenseHeader';
import ExpenseParticipants from './ExpenseParticipants';
import EditDeleteButtons from './EditDeleteButtons';
import ExpenseForm from './ExpenseForm';

const SplitwiseExpenseDetailPage = ({groupId, expenseId, fetchExpenses, action, handleAction, setEverythingToNull}) => {

    const [expense, setExpenseDetails] = useState(null);
    const [editExpense, setEditExpense] = useState(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);
    const [editMode, setEditMode] = useState(false);
    const [totalMismatch, setTotalMismatch] = useState('');
    const [connectionError, setConnectionError] = useState(''); 

    const fetchExpenseDetails = async () => {
        const token = sessionStorage.getItem('token');
        console.log("expense id"+expenseId);
        setError('');
        setConnectionError('');
        try {
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/expenses/${expenseId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
            });

            if (!response.ok) {
                
                const data = await response.text();
                throw new Error(data);
            }
            const data = await response.json();
            //.log("data", data);
            // Initialize each participant's checked status for editing
            const dataWithOutChecked = {
                ...data,
                participants: data.participants.map(participant => ({
                    ...participant,
                    //isChecked: true // Assuming initially all are checked
                }))
            };

            //console.log(dataWithOutChecked);

            //onsole.log("printing");
            setLoading(false);
            setEditExpense(dataWithOutChecked);
            setExpenseDetails(dataWithOutChecked);

        } catch (error) {
            setLoading(false);
            if (error instanceof TypeError) {
                
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                
                setError(error.message);
            }
        }
    };

    useEffect(() => {
        //console.log("inside useeffect");
        fetchExpenseDetails();
    }, [expenseId]);


    const handleInputChange = (name, value) => {
        setEditExpense(prev => ({
            ...prev,
            [name]: value
        }));
        
        if (name === "amount") {
            setTotalMismatch('');
        }
    };

    const handleParticipantAmountChange = (username, field, value) => {
        setEditExpense(prev => ({
            ...prev,
            participants: prev.participants.map(p =>
                p.username === username ? { ...p, [field]: parseFloat(value) } : p
            )
        }));
    };

    const handleParticipantCheckboxChange = (username) => {
        setEditExpense(prev => ({
            ...prev,
            participants: prev.participants.map(p =>
                p.username === username ? { ...p, isChecked: !p.isChecked } : p
            )
        }));
    };

    const handleUpdate = async () => {
        
        let totalContributions = 0;
        editExpense.participants.forEach(participant => {
            totalContributions = totalContributions + (parseFloat(participant.amountPaid) || 0);
        })
        if (totalContributions !== parseFloat(editExpense.amount)) {
            setTotalMismatch("Total expense amount must be equal to the sum of the total contributions by payers.");
            return; 
        }

        const participants = {};
        const payers = {};

        let payersCount = 0, participantCount = 0;

        let payerUsername="", participantUsername="";

        editExpense.participants.forEach(participant => {
            participants[participant.username] = participant.isChecked;
            if(participant.isChecked)
            {
                participantCount++;
                participantUsername = participant.username;
            }

            payers[participant.username] = parseFloat(participant.amountPaid) || 0;

            if(payers[participant.username] > 0)
            {
                payersCount++;
                payerUsername = participant.username;
            }
        });

        if(participantCount === 0)
        {
            setTotalMismatch("No participant selected");
            return;
        }
        else
        {
            if(participantCount === 1)
            {
                if(payersCount === 1 && participantUsername === payerUsername)
                {
                    setTotalMismatch("The only participant and payer are the same");
                    return;
                }
            }
        }

        const updatePayload = {
            ...editExpense,
            participants: participants,
            payers: payers
        };

        console.log("updatePayload",updatePayload);
        try {
            setError('');
            setConnectionError('');

            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/expenses/${expenseId}/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify(updatePayload)
            });

            if (!response.ok) {
                const data = await response.text();
                throw new Error(data);
            }

            const updatedExpense = await response.json();

            const dataWithOutChecked = {
                ...updatedExpense,
                participants: updatedExpense.participants.map(participant => ({
                    ...participant,
                    //isChecked: true // Assuming initially all are checked
                }))
            };

            setEditExpense(dataWithOutChecked);
            setExpenseDetails(dataWithOutChecked);
            //setExpenseDetails(updatedExpense);
            setEditMode(false);
            setTotalMismatch(''); // Reset on successful update

            //console.log(updatedExpense);

            toast.success('Updated successfully!');

            setTimeout(() => {
                fetchExpenses();
            }, 2000);
            //fetchExpenseDetails();
            /*setTimeout(() => {
                navigate(`/splitwise/groups/${groupId}`);
            }, 2000);*/
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                toast.error("Failed to Update");
            }
        }
    };


    //function to handle the deletion of expense
    const handleDelete = async () => {
        try {
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/expenses/${expenseId}/delete`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                }
            });

            if (!response.ok) {
                const data = await response.text();
                throw new Error(data);
            }

            setEverythingToNull();
            
            setTimeout(() => {
                fetchExpenses();
            }, 2000);

        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                toast.error('Failed to delete');
            }
        }
    };


    const handleRestore = async () => {

        try {
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/expenses/${expenseId}/restore`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                }
            });

            //console.log(response)
            if (!response.ok) {
                const data = await response.text();
                throw new Error(data);
            }

            setEverythingToNull();

            setTimeout(() => {
                fetchExpenses();
            }, 2000);
            
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                toast.error('Failed to restore');
            }
        }
    }

    const handleUpdatePayment = async () => {
        // Prepare payers and participants data
        const payers = {};
        editExpense.participants.forEach(participant => {
            if (participant.amountPaid > 0) {  // Check if the paid amount is greater than zero
                payers[participant.username] = parseFloat(editExpense.amount) || 0;; // Convert to string for precision
            }
        });


        // Filter participants to include only those who are checked as involved
        const participants = {};
        editExpense.participants.forEach(participant => {
            if (participant.isChecked) {  // Check if the participant is marked as involved
                participants[participant.username] = true;
            }
        });

        const updatePayload = {
            expenseName: expense.expenseName,
            amount: editExpense.amount,
            payers: payers,
            participants: participants,
            isPayment: true  
        };
    
        try {
            setError('');
            setConnectionError('');
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/expenses/${expenseId}/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify(updatePayload)
            });
    
            
            if (!response.ok) {
                const data = await response.text();
                throw new Error(data);
            }
    
            const updatedExpense = await response.json();
            setEditMode(false);
            toast.success('Payment updated successfully!');

            setTimeout(() => {
                fetchExpenses();
            }, 2000);
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                toast.error("Failed to Update");Error(error.message);
            }
        }
    };

    console.log("action in expenseDetail", action);

    useEffect(() => {
        if (action === 'restore') {
            handleRestore();
        } else if (action === 'delete') {
            handleDelete();
        }
    }, [action]);

    const handleCancelButton = () => {
        setTotalMismatch('');
        setEditMode(false);
        setEditExpense({ ...expense });
    }

    if(connectionError)
    {
        return (
            <div>{connectionError}</div>
        );
    }

    if (loading) return <p>Loading...</p>;
    if (error) return <p>{error}</p>;

    //console.log("totalMismatch", totalMismatch);

    return (
        <div className={styles.container}>
            {!editMode && (
                <div className={styles.details}>
                    <ExpenseHeader expense={expense} />
                    {!expense.isPayment && (<ExpenseParticipants participants={expense.participants} />)}
                    <EditDeleteButtons
                        isDeleted={expense.isDeleted}
                        gmRemovedDate={expense.gmRemovedDate}
                        setEditMode={setEditMode}
                        isPayment={expense.isPayment}
                        handleAction={handleAction}
                    />
                </div>
            )}
            
            {editMode && !expense.isPayment && (
                <div>
                    <ExpenseForm 
                        editExpense={editExpense}
                        handleInputChange={handleInputChange}
                        handleParticipantAmountChange={handleParticipantAmountChange}
                        handleParticipantCheckboxChange={handleParticipantCheckboxChange}
                    />
                    <div className={styles.buttonContainer}>
                        {totalMismatch && <p className={styles.warning}>{totalMismatch}</p>}
                        <button className={styles.saveButton} onClick={handleUpdate}>Save Changes</button>
                        <button className={styles.cancelButton} onClick={handleCancelButton}>Cancel</button>
                    </div>
                </div>
            )}

            {editMode && expense.isPayment && (
                <div className={styles.formContainer}>
                    <label htmlFor="amount" className={styles.paymentLabel}>Amount:</label>
                    <input
                        id="amount"
                        type="number"
                        value={editExpense.amount}
                        className={styles.paymentInput}
                        onChange={e => handleInputChange('amount', e.target.value)}
                    />
                    <div className={styles.buttonContainer}>
                        <button className={styles.saveButton} onClick={handleUpdatePayment}>Save Changes</button>
                        <button className={styles.cancelButton} onClick={handleCancelButton}>Cancel</button>
                    </div>
                </div>
            )}

            
            </div>
    );
};

export default SplitwiseExpenseDetailPage;

{/* <ConfirmModal
                isOpen={showConfirmModal}
                onClose={() => setShowConfirmModal(false)}
                onConfirm={modalAction === 'delete' ? handleDelete : handleRestore}
                message={`Are you sure you want to ${modalAction} this ${expense.isPayment ? 'Payment' : 'Expense'}?`}
            /> */}
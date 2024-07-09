import React, { useEffect, useState, useCallback } from 'react';
import { Navigate, useNavigate, useParams} from 'react-router-dom';
import styles from './SplitwiseExpenseDetailPage.module.css';
import ConfirmModal from '../../Modal/ConfirmModal';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ExpenseHeader from './ExpenseHeader';
import ExpenseParticipants from './ExpenseParticipants';
import EditDeleteButtons from './EditDeleteButtons';
import ExpenseForm from './ExpenseForm';

const SplitwiseExpenseDetailPage = () => {
    const { groupId, expenseId } = useParams();
    const [expense, setExpenseDetails] = useState(null);
    const [editExpense, setEditExpense] = useState(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);
    const [editMode, setEditMode] = useState(false);
    const [totalMismatch, setTotalMismatch] = useState(false);
    const [connectionError, setConnectionError] = useState('');
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const [modalAction, setModalAction] = useState(''); // 'delete' or 'restore'


    const navigate = useNavigate();

    const fetchExpenseDetails = async () => {
        const token = sessionStorage.getItem('token');
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
    }, []);


    const handleInputChange = (name, value) => {
        setEditExpense(prev => ({
            ...prev,
            [name]: value
        }));
        // Reset the mismatch error when amount is edited
        if (name === "amount") {
            setTotalMismatch(false);
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
            setTotalMismatch(true);
            return; 
        }

        const participants = {};
        const payers = {};

        editExpense.participants.forEach(participant => {
            participants[participant.username] = participant.isChecked;
            payers[participant.username] = parseFloat(participant.amountPaid) || 0;
        });

        const updatePayload = {
            ...editExpense,
            participants: participants,
            payers: payers
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
            setTotalMismatch(false); // Reset on successful update

            console.log(updatedExpense);

            toast.success('Updated successfully!');

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

            setShowConfirmModal(false);
            toast.success('Deleted successfully!');
            
            setTimeout(() => {
                navigate(`/splitwise/groups/${groupId}`);
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

            setShowConfirmModal(false);
            toast.success('Restored successfully!');

            setTimeout(() => {
                navigate(`/splitwise/groups/${groupId}`);
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
                navigate(`/splitwise/groups/${groupId}`);
            }, 2000);
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                toast.error("Failed to Update");Error(error.message);
            }
        }
    };
    
    if(connectionError)
    {
        return (
            <div>{connectionError}</div>
        );
    }

    if (loading) return <p>Loading...</p>;
    if (error) return <p>{error}</p>;

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Expense Details</h1>
            {!editMode && (
                <div className={styles.details}>
                    <ExpenseHeader expense={expense} />
                    {!expense.isPayment && (<ExpenseParticipants participants={expense.participants} />)}
                    <EditDeleteButtons
                        isDeleted={expense.isDeleted}
                        gmRemovedDate={expense.gmRemovedDate}
                        setEditMode={setEditMode}
                        setShowConfirmModal={setShowConfirmModal}
                        setModalAction={setModalAction}
                        isPayment={expense.isPayment}
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
                        totalMismatch={totalMismatch}
                    />
                    <div className={styles.buttonContainer}>
                        {totalMismatch && <p className="warning">Total expense amount must be equal to the sum of the total contributions by payers.</p>}
                        <button className={styles.saveButton} onClick={handleUpdate}>Save Changes</button>
                        <button className={styles.cancelButton} onClick={() => setEditMode(false)}>Cancel</button>
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
                        <button className={styles.cancelButton} onClick={() => setEditMode(false)}>Cancel</button>
                    </div>
                </div>
            )}

            <ConfirmModal
                isOpen={showConfirmModal}
                onClose={() => setShowConfirmModal(false)}
                onConfirm={modalAction === 'delete' ? handleDelete : handleRestore}
                message={`Are you sure you want to ${modalAction} this ${expense.isPayment ? 'Payment' : 'Expense'}?`}
            />
            <ToastContainer position="top-center" autoClose={5000} hideProgressBar={false} newestOnTop={false} closeOnClick rtl={false} pauseOnFocusLoss draggable pauseOnHover />
        </div>
    );
};

export default SplitwiseExpenseDetailPage;

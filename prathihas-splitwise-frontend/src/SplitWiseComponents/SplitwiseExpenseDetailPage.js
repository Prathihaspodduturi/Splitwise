import React, { useEffect, useState } from 'react';
import { Navigate, useNavigate, useParams } from 'react-router-dom';

const SplitwiseExpenseDetailPage = () => {
    const { groupId, expenseId } = useParams();
    const [expense, setExpenseDetails] = useState(null);
    const [editExpense, setEditExpense] = useState(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);
    const [editMode, setEditMode] = useState(false);
    const [totalMismatch, setTotalMismatch] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        const fetchExpenseDetails = async () => {
            try {
                const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/expenses/${expenseId}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                    },
                });

                if (!response.ok) {
                    throw new Error("Failed to load: Please try again later");
                }
                const data = await response.json();
                // Initialize each participant's checked status for editing
                const dataWithChecked = {
                    ...data,
                    participants: data.participants.map(participant => ({
                        ...participant,
                        isChecked: true // Assuming initially all are checked
                    }))
                };
                console.log(dataWithChecked);
                setExpenseDetails(dataWithChecked);
                setEditExpense(dataWithChecked);
                setLoading(false);
            } catch (error) {
                setError(error.message);
                console.error('Failed to fetch expense details:', error);
            }
        };
        fetchExpenseDetails();
    }, [groupId, expenseId]);

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
        const totalContributions = editExpense.participants.reduce((acc, curr) => acc + (curr.isChecked ? curr.amountPaid : 0), 0);
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
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/expenses/${expenseId}/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify(updatePayload)
            });

            if (!response.ok) {
                throw new Error('Failed to update expense');
            }

            const updatedExpense = await response.json();
            //setExpenseDetails(updatedExpense);
            setEditMode(false);
            setTotalMismatch(false); // Reset on successful update
            alert('Expense updated successfully!');
            navigate(`/splitwise/groups/${groupId}`);
        } catch (error) {
            setError(error.message);
            console.error('Failed to update expense details:', error);
        }
    };


    //function to handle the deletion of expense
    const handleDelete = async () => {
        if (window.confirm("Are you sure you want to delete this expense?")) {
            try {
                const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/expenses/${expenseId}/delete`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                    }
                });

                if (!response.ok) {
                    throw new Error('Failed to delete the expense');
                }

                alert('Expense deleted successfully!');
                navigate(`/splitwise/groups/${groupId}`);
            } catch (error) {
                setError(error.message);
                console.error('Failed to delete expense:', error);
            }
        }
    };


    const handleRestore = async () => {
        if (window.confirm("Are you sure you want to restore this expense?")) {
            try {
                const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/expenses/${expenseId}/restore`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                    }
                });

                if (!response.ok) {
                    throw new Error('Failed to restore the expense');
                }

                alert('Expense restored successfully!');
                navigate(`/splitwise/groups/${groupId}`);
            } catch (error) {
                setError(error.message);
                console.error('Failed to restore expense:', error);
            }
        }
    }

    if (loading) return <p>Loading...</p>;
    if (error) return <p>{error}</p>;

    return (
        <div>
            <h1>Expense Details</h1>
            {!editMode && (
                <>
                    <p>Name: {expense.expenseName}</p>
                    <p>Amount: ${expense.amount.toFixed(2)}</p>
                    <p>Added by: {expense.addedBy}</p>
                    <p>Date Created: {new Date(expense.dateCreated).toLocaleDateString()}</p>
                    {expense.updatedBy && <p>Last Updated By: {expense.updatedBy}</p>}
                    {expense.lastUpdatedDate && <p>Last Updated: {new Date(expense.lastUpdatedDate).toLocaleDateString()} at {new Date(expense.lastUpdatedDate).toLocaleTimeString()}</p>}
                    {expense.deletedBy && <p>Deleted By: {expense.deletedBy}</p>}
                    {expense.deletedDate && <p>Deleted On: {new Date(expense.deletedDate).toLocaleDateString()}</p>}
                </>
            )}
            {editMode ? (
                <>
                    <label>Expense Name:</label>
                    <input
                        type="text"
                        value={editExpense.expenseName}
                        onChange={e => handleInputChange('expenseName', e.target.value)}
                    />
                    <label>Amount:</label>
                    <input
                        type="number"
                        value={editExpense.amount}
                        onChange={e => handleInputChange('amount', e.target.value)}
                    />
                    <div>
                        <h3>Payers</h3>
                        {editExpense.participants.map(participant => (
                            <div key={participant.username}>
                                <label>{participant.username}:</label>
                                <input
                                    type="number"
                                    value={participant.amountPaid}
                                    onChange={e => handleParticipantAmountChange(participant.username, 'amountPaid', e.target.value)}
                                />
                            </div>
                        ))}
                    </div>
                    <div>
                        <h3>Participants</h3>
                        {editExpense.participants.map(participant => (
                            <div key={participant.username}>
                                <label>
                                    <input
                                        type="checkbox"
                                        checked={participant.isChecked}
                                        onChange={() => handleParticipantCheckboxChange(participant.username)}
                                    />
                                    {participant.username}
                                </label>
                            </div>
                        ))}
                    </div>
                    {totalMismatch && <p className="warning">Total expense amount must be equal to the sum of the total contributions by payers.</p>}
                    <button onClick={handleUpdate}>Save Changes</button>
                    <button onClick={() => setEditMode(false)}>Cancel</button>
                </>
            ) : (
                <>
                    {!expense.isDeleted ? (
                        <>
                            <button onClick={() => setEditMode(true)}>Edit Expense</button>
                            <button onClick={handleDelete}>Delete Expense</button>
                        </>
                    ) : (
                        <button onClick={handleRestore}>Restore Expense</button>
                    )}
                </>
            )}
        </div>
    );
};

export default SplitwiseExpenseDetailPage;

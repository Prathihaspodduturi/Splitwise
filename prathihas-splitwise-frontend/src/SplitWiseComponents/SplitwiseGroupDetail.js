// File: SplitWiseComponents/SplitwiseGroupDetail.js

import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { NavLink } from 'react-router-dom';

const SplitwiseGroupDetail = () => {
    const { groupId } = useParams();
    const navigate = useNavigate();
    const [group, setGroup] = useState(null);
    const [expenses, setExpenses] = useState([]);
    const [members, setMembers] = useState([]);

    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    const [showUpdateForm, setShowUpdateForm] = useState(false);
    const [newGroupName, setNewGroupName] = useState('');

    const [showAddMemberForm, setShowAddMemberForm] = useState(false);
    const [newUsername, setNewUsername] = useState('');

    const [showAddExpenseForm, setShowAddExpenseForm] = useState(false);
    const [newExpenseName, setNewExpenseName] = useState('');
    const [newExpenseAmount, setNewExpenseAmount] = useState('');
    const [payers, setPayers] = useState(new Map()); // Tracks amounts paid by each payer
    const [participants, setParticipants] = useState(new Map()); // Tracks participation status
    const [showPayers, setShowPayers] = useState(false); // Toggle for showing payers
    const [showParticipants, setShowParticipants] = useState(false); // Toggle for showing participants

    const [showGroupMembers, setShowGroupMembers] = useState(false);
    const [showExpenses, setShowExpenses] = useState(false);
    const [showDeletedExpenses, setShowDeletedExpenses] = useState(false);
    const [activeExpenses, setActiveExpenses] = useState([]);
    const [deletedExpenses, setDeletedExpenses] = useState([]);

    useEffect(() => {
        const fetchGroupDetails = async () => {
            try {
                const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                    },
                });

                if (!response.ok) {
                    throw new Error('Failed to fetch group details');
                }

                const data = await response.json();
                setMembers(data.members);
                setGroup(data.group);
                console.log(data.detailedExpenses)
                const sortedExpenses = data.detailedExpenses.sort((a, b) => new Date(a.dateCreated) - new Date(b.dateCreated));
                setExpenses(sortedExpenses);
                const tempActiveExpenses = sortedExpenses.filter(exp => !exp.deleted);
                const tempDeletedExpenses = sortedExpenses.filter(exp => exp.deleted);
                setActiveExpenses(tempActiveExpenses);
                setDeletedExpenses(tempDeletedExpenses);
                console.log(expenses);
                setNewGroupName(data.groupName);
            } catch (error) {
                setError(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        fetchGroupDetails();
    }, [groupId]);

    const handleUpdateGroupName = async (event) => {
        event.preventDefault();
        try {
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify({ groupName: newGroupName })
            });

            if (!response.ok) {
                throw new Error('Failed to update group name');
            }

            alert('Group name updated successfully!');
            const updatedGroup = await response.json();
            setGroup(updatedGroup);
            setNewGroupName(updatedGroup.groupName);
            setShowUpdateForm(false); // Hide the form after successful update
        } catch (error) {
            setError(error.message);
        }
    };

    const handleAddMember = async (event) => {
        event.preventDefault();
        try {
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/addmember`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify({ "newUsername" : newUsername })
            });

            if (!response.ok) {
                throw new Error('Failed to add member');
            }

            alert('Member added successfully!');
            const data = await response.json();
            setMembers(data);
            
            setNewUsername('');
            setShowAddMemberForm(false); // Hide the form after successful addition
            console.log(members);
        } catch (error) {
            setError(error.message);
        }
    };

    const handleDeleteGroup = async () => {
        if (window.confirm('Are you sure you want to delete this group?')) {
            try {
                const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/delete`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                    },
                });

                if (!response.ok) {
                    throw new Error('Failed to delete group');
                }

                alert('Group deleted successfully!');
                navigate('/splitwise/groups');
            } catch (error) {
                setError(error.message);
            }
        }
    };

    const handleAddExpense = async (event) => {
        event.preventDefault();

        if(payers.size === 0)
        {
            alert("No Payers selected");
            return;
        }

        if(participants.size === 0)
        {
            alert("No Participants selected");
            return;
        }

        let totalContributions = 0;
        payers.forEach((amount, username) => {
            totalContributions += parseFloat(amount || 0);
        });

        const totalExpense = parseFloat(newExpenseAmount);

        if (totalContributions !== totalExpense) {
            alert("The sum of all contributions must equal the total expense amount.");
            return;  
        }

        const payersObject = Object.fromEntries(payers);
        const participantsObject = Object.fromEntries(participants);

        const expenseData = {
            "groupId" : groupId,
            "expenseName" : newExpenseName,
            "amount" : newExpenseAmount,
            "payers" : payersObject,
            "participants" : participantsObject
        };

        try{
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/addExpense`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify(expenseData)
            });

            if(!response.ok){
                throw new Error("Failed to add expense");
            }

            const newExpense = await response.json();
            console.log("newExpense", newExpense);
            const updatedExpenses = [...expenses, newExpense];
            setExpenses(updatedExpenses);

            alert('Expense added successfully!');

            setNewExpenseName('');
            setNewExpenseAmount('');
            setPayers(new Map());
            setParticipants(new Map());
            setShowAddExpenseForm(false);
            //console.log("addexpense" + expenses);
        }
        catch(error)
        {
            setError(error.message);
        }
    }

    const handleRestoreExpense = async (expenseId) => {
        if (window.confirm('Are you sure you want to restore this expense?')) {
            try {
                const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/expenses/${expenseId}/restore`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                    },
                });
    
                if (!response.ok) {
                    throw new Error('Failed to restore expense');
                }
    
                alert('Expense restored successfully!');
            } catch (error) {
                setError(error.message);
            }
        }
    };

    const togglePayers = () => setShowPayers(!showPayers);
    const toggleParticipants = () => setShowParticipants(!showParticipants);
    const toggleGroupMembers = () => {
        setShowGroupMembers(prevShowGroupMembers => !prevShowGroupMembers);
      };


    if (isLoading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div>
            <NavLink to="/splitwise/logout">Logout</NavLink>
            <h1>Group Details</h1>
            {group && (
                <>
                    <h2>{group.groupName}</h2>
                    <p>Description: {group.groupDescription}</p>
                    <div onClick={() => setShowUpdateForm(!showUpdateForm)} style={{ cursor: 'pointer', color: 'blue' }}>Update Group Name</div>
                    {showUpdateForm && (
                        <form onSubmit={handleUpdateGroupName}>
                            <input
                                type="text"
                                value={newGroupName}
                                onChange={(e) => setNewGroupName(e.target.value)}
                            />
                            <button type="submit">Submit</button>
                        </form>
                    )}
                    <div onClick={() => setShowAddMemberForm(!showAddMemberForm)} style={{ cursor: 'pointer', color: 'blue' }}>Add Member</div>
                    {showAddMemberForm && (
                        <form onSubmit={handleAddMember}>
                            <input
                                type="username"
                                placeholder="Username"
                                value={newUsername}
                                onChange={(e) => setNewUsername(e.target.value)}
                            />
                            <button type="submit">Add</button>
                        </form>
                    )}
                    <div onClick={handleDeleteGroup} style={{ cursor: 'pointer', color: 'red' }}>Delete Group</div>
                </>
            )}
            <div onClick={() => setShowExpenses(!showExpenses)} style={{ cursor: 'pointer', color: 'blue' }}>
                {showExpenses ? 'Hide Expenses' : 'Show Expenses'}
            </div>
            {showExpenses && (
                <div style={{ padding: '10px', border: '1px solid #ccc' }}>
                    <h3>Expenses</h3>
                    {expenses.length > 0 ? (
                        <ul>
                            {activeExpenses.map((expense, index) => (
                                <li key={index}>
                                    <strong>
                                        <NavLink to={`/splitwise/groups/${groupId}/expenses/${expense.id}`}>
                                            {expense.expenseName}
                                        </NavLink>
                                    </strong> - ${expense.amount.toFixed(2)}
                                    (Date: {new Date(expense.dateCreated).toLocaleDateString()})
                                    {expense.notInvolved ?
                                        <p>Not involved</p> :
                                        <p>You {expense.involved >= 0 ? 'get back' : 'owe'} ${Math.abs(expense.involved).toFixed(2)}</p>
                                    }
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No active expenses recorded.</p>
                    )}
                </div>
            )}
            <div onClick={() => setShowDeletedExpenses(!showDeletedExpenses)} style={{ cursor: 'pointer', color: 'blue' }}>
                {showDeletedExpenses ? 'Hide Deleted Expenses' : 'Show Deleted Expenses'}
            </div>
            {showDeletedExpenses && (
                <div style={{ padding: '10px', border: '1px solid #ccc' }}>
                    <h3>Deleted Expenses</h3>
                    {deletedExpenses.length > 0 ? (
                        <ul>
                        {deletedExpenses.map((expense, index) => (
                            <li key={index}>
                                <strong>
                                    <NavLink to={`/splitwise/groups/${groupId}/expenses/${expense.id}`}>
                                        {expense.expenseName}
                                    </NavLink>
                                </strong> - ${expense.amount.toFixed(2)}
                                (Date: {new Date(expense.dateCreated).toLocaleDateString()})
                                {expense.notInvolved ?
                                    <p>Not involved</p> :
                                    <p>You {expense.involved >= 0 ? 'get back' : 'owe'} ${Math.abs(expense.involved).toFixed(2)}</p>
                                }
                            </li>
                        ))}
                    </ul>
                    ) : (
                        <p>No deleted expenses.</p>
                    )}
                </div>
            )}
            <div onClick={toggleGroupMembers} style={{ cursor: 'pointer', color: 'blue' }}>
                {showGroupMembers ? 'Hide Group Members' : 'Show Group Members'}
            </div>
            {showGroupMembers && (
                <div style={{ padding: '10px', border: '1px solid #ccc' }}>
                    <h3>Group Members</h3>
                    <ul>
                        {members.map(member => (
                            <li key={member.username}>{member.username}</li>
                        ))}
                    </ul>
                </div>
            )}
            <div onClick={() => setShowAddExpenseForm(!showAddExpenseForm)} style={{ cursor: 'pointer' }}>Add Expense</div>
            {showAddExpenseForm && (
                <form onSubmit={handleAddExpense}>
                    <input
                        type="text"
                        placeholder="Expense Name"
                        value={newExpenseName}
                        onChange={e => setNewExpenseName(e.target.value)}
                        required
                    />
                    <input
                        type="number"
                        placeholder="Total Amount"
                        value={newExpenseAmount}
                        onChange={e => setNewExpenseAmount(e.target.value)}
                        required
                    />
                    <div onClick={togglePayers} style={{ cursor: 'pointer' }}>Add Payers</div>
                    {showPayers && members.map(member => (
                        <div key={member.username}>
                            <label>{member.username}</label>
                            <input
                                type="number"
                                placeholder="Amount Paid"
                                value={payers.get(member.username) || ''}
                                onChange={e => setPayers(new Map(payers.set(member.username, e.target.value)))}
                            />
                        </div>
                    ))}
                    <div onClick={toggleParticipants} style={{ cursor: 'pointer' }}>Add Participants</div>
                    {showParticipants && members.map(member => (
                        <div key={member.username}>
                            <label>{member.username}</label>
                            <input
                                type="checkbox"
                                checked={!!participants.get(member.username)}
                                onChange={e => setParticipants(new Map(participants.set(member.username, e.target.checked)))}
                            />
                        </div>
                    ))}
                    <button type="submit">Done</button>
                </form>
            )}
        </div>
    );
}   

export default SplitwiseGroupDetail;

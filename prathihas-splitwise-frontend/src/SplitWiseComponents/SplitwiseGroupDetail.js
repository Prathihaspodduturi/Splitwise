// File: SplitWiseComponents/SplitwiseGroupDetail.js

import React, { useEffect, useState, useCallback } from 'react';
import { FaEdit } from 'react-icons/fa'; // Import the edit icon from a library
import { useParams, useNavigate } from 'react-router-dom';
import { NavLink } from 'react-router-dom';
import styles from './SplitwiseGroupDetail.module.css';


const SplitwiseGroupDetail = () => {

    const currentUser = sessionStorage.getItem('username');

    const { groupId } = useParams();
    const navigate = useNavigate();
    const [group, setGroup] = useState(null);
    const [gmDetails, setGmDetails] = useState(null); 
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
    const [showExpenses, setShowExpenses] = useState(true);
    const [showDeletedExpenses, setShowDeletedExpenses] = useState(false);
    const [activeExpenses, setActiveExpenses] = useState([]);
    const [deletedExpenses, setDeletedExpenses] = useState([]);
    const [showBalances, setShowBalances] = useState(false);
    const [balances, setBalances] = useState([]);
    const [showTotalExpenses, setShowTotalExpenses] = useState(false);
    const [totalGroupExpenses, setTotalGroupExpenses] = useState(0);
    const [userTotalExpenses, setUserTotalExpenses] = useState(0);
    const [paidAmount, setPaidAmount] = useState(0);
    const [showEditOptions, setShowEditOptions] = useState(false);

    const [showModal, setShowModal] = useState(false);
    const [blurBackground, setBlurBackground] = useState(false);

    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [selectedBalance, setSelectedBalance] = useState(null);
    const [paymentAmount, setPaymentAmount] = useState('');



    const fetchGroupDetails = useCallback(async () => {
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
            console.log("memebrs", data.members);
            setMembers(data.members);
            setGroup(data.group);
            setGmDetails(data.gmDetails); 

            const filteredExpenses = data.detailedExpenses.filter(exp => {
                const expDate = new Date(exp.dateCreated);
                const addedDate = new Date(data.gmDetails.addedDate);
                const removedDate = data.gmDetails.removedDate ? new Date(data.gmDetails.removedDate) : new Date();
                return expDate >= addedDate && expDate <= removedDate;
            });


            const sortedExpenses = filteredExpenses.sort((a, b) => -(new Date(a.dateCreated) - new Date(b.dateCreated)));
            setExpenses(sortedExpenses);
            const tempActiveExpenses = sortedExpenses.filter(exp => !exp.deleted);
            const tempDeletedExpenses = sortedExpenses.filter(exp => exp.deleted);
            setActiveExpenses(tempActiveExpenses);
            setDeletedExpenses(tempDeletedExpenses);
            setNewGroupName(data.groupName);
            console.log(data.transactions);
            setBalances(data.transactions);
            let groupTotal = 0;
            let userTotal = 0;
            let paidTotal = 0;
            //const username = sessionStorage.getItem('username'); // Replace "currentUser" with the actual username of the logged-in user

            sortedExpenses.forEach(expense => {
                if(!expense.isPayment)
                {
                    groupTotal += parseFloat(expense.amount);
                }
                if (!expense.deleted && !expense.notInvolved && !expense.isPayment) {
                    if(expense.involved < 0){
                        userTotal = userTotal - parseFloat(expense.involved);
                    }
                    else
                    {
                        userTotal = userTotal + parseFloat(expense.amount) - parseFloat(expense.involved);
                        paidTotal = paidTotal + parseFloat(expense.involved);
                    }
                }
            });

            setTotalGroupExpenses(groupTotal);
            setUserTotalExpenses(userTotal);
            setPaidAmount(paidTotal);
        } catch (error) {
            setError(error.message);
        } finally {
            setIsLoading(false);
        }
    }, [groupId]);

    useEffect(() => {
        fetchGroupDetails();
    }, [fetchGroupDetails]);

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
            setShowExpenses(true);
            setBlurBackground(false);
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
            setShowAddMemberForm(false);
            fetchGroupDetails(); 
            console.log(members);
        } catch (error) {
            setError(error.message);
        }
    };

    const handleSettleGroup = async () => {

        const hasOutstandingBalances = balances.some(balance => balance.amount !== 0);

        if (hasOutstandingBalances) {
            alert('Cannot settle group while there are outstanding balances.');
            return;
        }

        if (window.confirm('Are you sure you want to delete this group?')) {
            try {
                const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/settlegroup`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                    },
                });

                if (!response.ok) {
                    throw new Error('Failed to settle up group');
                }

                alert('Group settled successfully!');
                navigate('/splitwise/groups');
            } catch (error) {
                setError(error.message);
            }
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

        let hasParticipants = false;
        for (let value of participants.values()) {
            if (value === true) {
                hasParticipants = true;
                break;
            }
        }

        if (!hasParticipants) {
            alert("No Participants selected. Please select at least one participant.");
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
            "participants" : participantsObject,
            "isPayment" : false
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
            fetchGroupDetails();
            setShowExpenses(true);
            //console.log("addexpense" + expenses);
        }
        catch(error)
        {
            setError(error.message);
        }
    }


    const handlePayment = async (balance) => {
        const paymentData = {
            "groupId": groupId,
            "expenseName": `${balance.fromUser} paid ${balance.toUser}`,
            "amount": paymentAmount,
            "payers": { [balance.fromUser]: paymentAmount },
            "participants": { [balance.toUser]: true },  // Assuming only the toUser is involved as the receiver
            "isPayment" : true
        };
    
        try {
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/addExpense`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify(paymentData)
            });
    
            if (!response.ok) {
                throw new Error("Failed to record payment");
            }
    
            const newExpense = await response.json();
            console.log("Payment recorded as expense", newExpense);
            alert('Payment recorded successfully!');
    
            setShowPaymentModal(false);
            // Refresh data after payment
            fetchGroupDetails();
        } catch (error) {
            console.error("Error recording payment:", error);
            setError(error.message);
        }
    };


    const handleRemoveMember = async (memberUsername) => {

        const netBalance = balances.reduce((acc, balance) => {
            if (balance.fromUser === memberUsername) {
                return acc - balance.amount;
            } else if (balance.toUser === memberUsername) {
                return acc + balance.amount;
            }
            return acc;
        }, 0);

    
        // Check if the net balance is zero
        if (netBalance !== 0) {
            if(currentUser === memberUsername)
            {
                alert(`Balances not settled up. Please settle all balances before leaving.`);
                return;
            }
            alert(`Balances not settled up for ${memberUsername}. Please settle all balances before removing.`);
            return;
        }

        if (window.confirm(`Are you sure you want to remove ${memberUsername} from the group?`)) {
            try {
                const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/removemember`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                    },
                    body: JSON.stringify({ username: memberUsername })
                });
    
                if (!response.ok) {
                    throw new Error('Failed to remove member');
                }
    
                alert(`${memberUsername} has been removed successfully!`);
                // Re-fetch the group details to update the list of members
                fetchGroupDetails();
            } catch (error) {
                console.error('Failed to remove member:', error);
                setError(error.message);
            }
        }
    };
    
    


    // Function to toggle balances visibility
    const toggleBalances = () => {
        setShowBalances(true);
        setShowAddMemberForm(false);
        setShowDeletedExpenses(false);
        setShowGroupMembers(false);
        setShowAddExpenseForm(false);
        setShowExpenses(false);
        setShowTotalExpenses(false);
        setShowEditOptions(false);
    };

    // Function to toggle add member form visibility within Members section
    const toggleAddMemberForm = () => {
        setShowAddMemberForm(prev => !prev);
        // When showing the add member form, we keep the members list visible
    };

    // Function to toggle deleted expenses visibility
    const toggleDeletedExpenses = () => {
        setShowDeletedExpenses(true);
        setShowBalances(false);
        setShowAddMemberForm(false);
        setShowGroupMembers(false);
        setShowAddExpenseForm(false);
        setShowExpenses(false);
        setShowTotalExpenses(false);
        setShowEditOptions(false);
    };

    // Function to toggle group members visibility
    const toggleMembers = () => {
        setShowGroupMembers(true);
        setShowBalances(false);
        setShowAddMemberForm(false);
        setShowDeletedExpenses(false);
        setShowAddExpenseForm(false);
        setShowExpenses(false);
        setShowTotalExpenses(false);
        setShowEditOptions(false);
    };

    // Function to toggle add expense form visibility
    const toggleAddExpense = () => {
        setShowAddExpenseForm(true);
        setShowExpenses(false);
        setShowBalances(false);
        setShowAddMemberForm(false);
        setShowDeletedExpenses(false);
        setShowGroupMembers(false);
        setShowTotalExpenses(false);
        setShowEditOptions(false);
        //console.log("showExpenses", showExpenses);
    };

    const toggleExpenses = () => {
        setShowExpenses(true);
        setShowAddExpenseForm(false);
        setShowBalances(false);
        setShowAddMemberForm(false);
        setShowDeletedExpenses(false);
        setShowGroupMembers(false);
        setShowTotalExpenses(false);
        setShowEditOptions(false);
    }

    const cancelAddExpense = () => {
        setShowAddExpenseForm(false);  // This will hide the form
        setShowExpenses(true);
        setPayers(new Map());
        setParticipants(new Map());
        setShowEditOptions(false);
    };
    
    const toggleTotalExpenses = () => {

        console.log("totalExpense", totalGroupExpenses);
        console.log("userExpenses", userTotalExpenses);

        setShowTotalExpenses(true);

        setShowExpenses(false);
        setShowAddExpenseForm(false);
        setShowBalances(false);
        setShowAddMemberForm(false);
        setShowDeletedExpenses(false);
        setShowGroupMembers(false);
        setShowEditOptions(false);
        console.log("showTotalExpenses", showTotalExpenses);
    }

    const toggleUpdateForm = () => {

        setShowUpdateForm(true);
        setShowTotalExpenses(false);
        setShowExpenses(false);
        setShowAddExpenseForm(false);
        setShowBalances(false);
        setShowAddMemberForm(false);
        setShowDeletedExpenses(false);
        setShowGroupMembers(false);
        setShowEditOptions(false);
    }

    const toggleEditIconForm = () => {

        setShowEditOptions(true);
        setShowEditOptions(!showEditOptions);
        setBlurBackground(!blurBackground);  // This will toggle the blur eff
        
    }

    const cancelUpdatingGroupName = () => {
        setShowUpdateForm(false);
        setShowEditOptions(true);
    }

    const cancelShowEditOptions = () => {
        setShowEditOptions(false);
        setBlurBackground(false);
    }    
    
    const togglePayers = () => {
        setShowPayers(!showPayers);
        setShowParticipants(false);
    }
    const toggleParticipants = () => 
    {
        setShowParticipants(!showParticipants);
        setShowPayers(false);
    }

    const togglePayerModal = () => {
        setShowPayers(!showPayers);
        //setBlurBackground(true); // Ensure background is blurred
         // Show payer modal
        setShowParticipants(false); // Hide participants modal
    };

    const toggleParticipantModal = () => {
        setShowParticipants(true)
        //setBlurBackground(true); // Ensure background is blurred
        ; // Show participant modal
        setShowPayers(false); // Hide payer modal
    };

    const openPaymentModal = (balance) => {
        setSelectedBalance(balance);
        setPaymentAmount(balance.amount.toString());
        setShowPaymentModal(true);
    };
    
    const closePaymentModal = () => {
        setShowPaymentModal(false);
        setSelectedBalance(null);
        setPaymentAmount('');
    };
    



    if (isLoading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;


    if(showUpdateForm)
    {
        return (
            <div>
                {showUpdateForm && (
                    <form onSubmit={handleUpdateGroupName} className='form-container'>
                        <label htmlFor="newGroupName">New Name:</label>
                        <input
                            type="text"
                            value={newGroupName}
                            onChange={(e) => setNewGroupName(e.target.value)}
                        />
                        <div className="button-group">
                        <button type="submit">Submit</button>
                        <button type="button" onClick={cancelUpdatingGroupName}>Cancel</button>
                        </div>
                    </form>
                )}
            </div>
        ); 
    }


    if(showAddExpenseForm) {
        return (
            <div className={`${styles.appContainer} ${blurBackground ? styles.blurred : ''}`}>
                {showAddExpenseForm && (
                    <div className={styles.addExpenseContainer}>
                        <form onSubmit={handleAddExpense} className={styles.formContainerAddExpense}>
                            <input
                                type="text"
                                className={styles.inputAddExpense}
                                placeholder="Expense Name"
                                value={newExpenseName}
                                onChange={e => setNewExpenseName(e.target.value)}
                                required
                            />
                            <input
                                type="number"
                                className={styles.inputAddExpense}
                                placeholder="Total Amount"
                                value={newExpenseAmount}
                                onChange={e => setNewExpenseAmount(e.target.value)}
                                required
                            />
                            <div onClick={togglePayers} className={`${styles.buttonAddExpense} ${styles.flexButtonAddExpense}`}>
                                Add Payers
                            </div>

                            
                            {showPayers && (
                                <div>
                                    <div>
                                        {members.map(member => (
                                            <div key={member.username} className={styles.inputGroup}>
                                                <label>{member.username}</label>
                                                <input
                                                    type="number"
                                                    value={payers.get(member.username) || ''}
                                                    onChange={e => setPayers(new Map(payers.set(member.username, e.target.value)))}
                                                />
                                            </div>
                                        ))}
                                        <button onClick={() => setShowPayers(false)}>Close</button>
                                    </div>
                                </div>
                            )}

    
                            <div onClick={toggleParticipants} className={`${styles.buttonAddExpense} ${styles.flexButtonAddExpense}`}>
                                Add Participants
                            </div>


                            {showParticipants && (
                                <div>
                                    <div>
                                        {members.map(member => (
                                            <div key={member.username} className={styles.inputGroup}>
                                                <label>{member.username}</label>
                                                <input
                                                    type="checkbox"
                                                    checked={!!participants.get(member.username)}
                                                    onChange={e => setParticipants(new Map(participants.set(member.username, e.target.checked)))}
                                                />
                                            </div>
                                        ))}
                                        <button onClick={() => setShowParticipants(false)}>Close</button>
                                    </div>
                                </div>
                            )}
                            
                            <div className={styles.flexRowAddExpense}>
                                <button type="submit" className={`${styles.buttonSubmitAddExpense} ${styles.flexButtonAddExpense}`}>Done</button>
                                <button type="button" onClick={cancelAddExpense} className={`${styles.buttonCancelAddExpense} ${styles.flexButton}`}>
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                )}
            </div>
        );
    }


    if (showPaymentModal) {
        return (
            <div className={styles.paymentModal}>
                <h4>{selectedBalance.fromUser} to {selectedBalance.toUser}</h4>
                <div className={styles.paymentModalContent}>
                    <label htmlFor="paymentAmount">Amount to Pay:</label>
                    <input
                        type="number"
                        id="paymentAmount"
                        className={styles.paymentModalInput}
                        value={paymentAmount}
                        onChange={e => setPaymentAmount(e.target.value)}
                        placeholder="Enter amount"
                    />
                    <button onClick={() => handlePayment(selectedBalance)} className={styles.paymentConfirmButton}>Confirm Payment</button>
                    <button onClick={closePaymentModal} className={styles.paymentCancelButton}>Cancel</button>
                </div>
            </div>
        );
    }
    
    

    return (
        <div>

    <div className={`${styles.appContainer} ${blurBackground ? styles.blurred : ''}`}>
                <NavLink to="/splitwise/logout" className={styles.logoutLink}>Logout</NavLink>

                <div className={styles.groupNameContainer}>
                    <h2>{group.groupName}</h2>
                    {gmDetails.removedDate === null && <FaEdit className={styles.editIcon} onClick={toggleEditIconForm} />}
                    {group.settledUp && <p>Group was settled by {group.settledBy} on {group.settledDate}</p>}
                    {gmDetails.removedBy !== null && gmDetails.removedDate !== null && (
                    <p className={styles.removalInfo}>
                        You were removed by {gmDetails.removedBy} on {new Date(gmDetails.removedDate).toLocaleDateString()}
                    </p>
                )}
                </div>

                <div className={styles.optionsContainer}>
                    <div onClick={toggleExpenses} className={styles.optionLink}>
                        Expenses
                    </div>
                    <div onClick={toggleBalances} className={styles.optionLink}>
                        {'Balances'}
                    </div>
                    <div onClick={toggleDeletedExpenses} className={styles.optionLink}>
                        {'Deleted Expenses'}
                    </div>
                    <div onClick={toggleMembers} className={styles.optionLink}>
                        Members
                    </div>
                    <div onClick={toggleTotalExpenses} className={styles.optionLink}>
                        Total Expenses
                    </div>
                </div>

                {showExpenses && (
                    <div className={styles.expensesSection}>
                        {(!group.settledUp && gmDetails.removedDate === null)&& <button onClick={toggleAddExpense} className={styles.addExpenseButton}>
                            Add Expense
                        </button> }
                        {expenses.length > 0 ? (
                            <ul>
                                {activeExpenses.map((expense, index) => (
                                    <li key={index} className={styles.expenseItem}>
                                    {expense.isPayment ? (
                                        <div className={styles.paymentItem}>
                                            <strong>
                                                <NavLink to={`/splitwise/groups/${groupId}/expenses/${expense.id}`} className={styles.paymentDetails}>
                                                    {expense.expenseName} (Payment)
                                                </NavLink>
                                            </strong>
                                            <div className={styles.paymentDetails}>
                                                Paid: ${expense.amount.toFixed(2)} -
                                                Date: {new Date(expense.dateCreated).toLocaleDateString()}
                                            </div>
                                        </div>
                                    ) :
                                    (
                                    <>
                                        <strong>
                                        <NavLink to={`/splitwise/groups/${groupId}/expenses/${expense.id}`}>
                                            {expense.expenseName}
                                        </NavLink>
                                    </strong>
                                    <div className={styles.expenseItemDetails}>
                                        ${expense.amount.toFixed(2)} - 
                                        Date: {new Date(expense.dateCreated).toLocaleDateString()}
                                        {expense.notInvolved ?
                                            (<p className={styles.expenseNeutral}>Not involved</p>)
                                            :
                                            (<p className={expense.involved >=0 ? styles.expenseGetBack : styles.expenseOwe}>
                                                You {expense.involved >= 0 ? 'get back' : 'owe'} ${Math.abs(expense.involved).toFixed(2)}
                                            </p>)
                                        }
                                    </div>
                                    </>
                                    )}
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>No active expenses recorded.</p>
                        )}
                    </div>
                )}
                
                {showDeletedExpenses && (
                    <div className={styles.deletedExpensesContainer}>
                        <h3>Deleted Expenses</h3>
                        {deletedExpenses.length > 0 ? (
                            <ul>
                            {deletedExpenses.map((expense, index) => (
                                <li key={index} className={styles.deletedExpensesItem}>
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


                {showGroupMembers && (
                    <div className={styles.membersContainer}>
                        <h3>Group Members</h3>
                        <ul>
                            {members.filter(member => member.removedBy === null).map(member => ( 
                                <li key={member.username} className={styles.membersItem}>
                                    <span className={styles.username}>{member.username}</span>
                                    {(!group.settledUp && gmDetails.removedDate === null) && (
                                        currentUser === member.username ? 
                                            <button onClick={() => handleRemoveMember(member.username)} className={styles.removeMemberButton}>Leave Group</button>
                                            :
                                            <button onClick={() => handleRemoveMember(member.username)} className={styles.removeMemberButton}>
                                                Remove
                                            </button>
                                    )}
                                </li>
                            ))}
                        </ul>

                        {(!group.settledUp && gmDetails.removedDate === null) && <div onClick={toggleAddMemberForm} className={styles.addMemberButton} >
                        {showAddMemberForm ? 'Hide Add Member Form' : 'Add Member'}
                        </div>}
                        {showAddMemberForm && (
                            <form onSubmit={handleAddMember} className={styles.addMemberForm}>
                                <input
                                    type="username"
                                    placeholder="Username"
                                    value={newUsername}
                                    onChange={(e) => setNewUsername(e.target.value)} required
                                    className={styles.addMemberFormInput}
                                />
                                <div className={styles.addMemberFormButtonGroup}>
                                    <button type="submit" className={styles.addMemberFormButton}>Add</button>
                                    <button type="button" onClick={() => setShowAddMemberForm(false)} className={styles.addMemberFormButton}>Cancel</button>
                                </div>
                            </form>
                        )}
                    </div>
                )}

                
                {showBalances && !isLoading && (
                <div className={styles.listBalancesContainer}>
                <h3>Balances</h3>
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
                )}

                {showTotalExpenses && 
                    (
                        <div className={styles.totalExpensesContainer}>
                        <h3 className={styles.totalExpensesHeader}>Total Expenses Summary</h3>
                        <div className={styles.totalExpensesItem}>
                            <span>Total Group Expenses:</span>
                            <span className={styles.totalExpensesValue}>${totalGroupExpenses.toFixed(2)}</span>
                        </div>
                        <div className={styles.totalExpensesItem}>
                            <span>Your Total Expenses:</span>
                            <span className={styles.totalExpensesValue}>${userTotalExpenses.toFixed(2)}</span>
                        </div>
                        <div className={styles.totalExpensesItem}>
                            <span>Total Amount Paid by you:</span>
                            <span className={styles.totalExpensesValue}>${paidAmount.toFixed(2)}</span>
                        </div>
                    </div>
                    )
                }

            </div>


            {showEditOptions && 
            (<div className={styles.modalShowEditOptions}>
                    <button className={styles.editOptionButton} onClick={toggleUpdateForm}>
                        Change Group Name
                    </button>
                    <button className={styles.editOptionButton}  onClick={handleSettleGroup}>
                        SettleUp Group
                    </button>
                    <button className={styles.editOptionButton}  onClick={handleDeleteGroup}>
                        Delete Group
                    </button>
                    <div className="button-group">
                    <button className={styles.editCancelButton} type="submit" onClick={cancelShowEditOptions}>back</button>
                    </div>
                </div>
                )
            }

        </div>
        
    );
}   

export default SplitwiseGroupDetail;

/*

{showGroupMembers && (
                    <div className={styles.membersContainer}>
                        <h3>Group Members</h3>
                        <ul>
                            {members.map(member => (
                                <li key={member.username} className={styles.membersItem}>
                                    <span className={styles.username}>{member.username}</span>
                                    {!group.settledUp && (
                                        currentUser === member.username ? 
                                            <button onClick={() => handleRemoveMember(member.username)} className={styles.removeMemberButton}>Leave Group</button>
                                            :
                                            <button onClick={() => handleRemoveMember(member.username)} className={styles.removeMemberButton}>
                                                Remove
                                            </button>
                                    )}
                                </li>
                            ))}
                        </ul>
                        {group.settledUp && <div onClick={toggleAddMemberForm} className={styles.addMemberButton} >
                        {showAddMemberForm ? 'Hide Add Member Form' : 'Add Member'}
                        </div> }
                        {showAddMemberForm && (
                            <form onSubmit={handleAddMember} className={styles.addMemberForm}>
                                <input
                                    type="username"
                                    placeholder="Username"
                                    value={newUsername}
                                    onChange={(e) => setNewUsername(e.target.value)} required
                                    className={styles.addMemberFormInput}
                                />
                                <div className={styles.addMemberFormButtonGroup}>
                                    <button type="submit" className={styles.addMemberFormButton}>Add</button>
                                    <button type="button" onClick={() => setShowAddMemberForm(false)} className={styles.addMemberFormButton}>Cancel</button>
                                </div>
                            </form>
                        )}
                    </div>
                )}
*/
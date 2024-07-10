// File: SplitWiseComponents/SplitwiseGroupDetail.js

import React, { useEffect, useState, useCallback } from 'react';
import { FaEdit } from 'react-icons/fa'; // Import the edit icon from a library
import { useParams, useNavigate } from 'react-router-dom';
import { NavLink } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import styles from './SplitwiseGroupDetail.module.css';
import Balances from './Balances';
import GroupMembers from './GroupMembers';
import ExpensesList from './ExpensesList';
import ConfirmModal from '../../Modal/ConfirmModal';
import styles1 from '../../toastStyles.module.css';


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
    
    const [showEditOptions, setShowEditOptions] = useState(false);
    const [connectionError, setConnectionError] = useState('');

    const [blurBackground, setBlurBackground] = useState(false);

    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [selectedBalance, setSelectedBalance] = useState(null);
    const [paymentAmount, setPaymentAmount] = useState('');

    const [isConfirmModalOpen, setIsConfirmModalOpen] = useState(false);
    const [memberToRemove, setMemberToRemove] = useState(null);


    const fetchGroupDetails = useCallback(async () => {
        setError('');
        setConnectionError('');
        try {
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
            });

            if (!response.ok) {
                const data = await response.text();
                throw new Error(data);
            }

            const data = await response.json();
            //console.log("data", data);
            setMembers(data.members);
            console.log("detailed", JSON.stringify(data.detailedExpenses, null, 2));
            
            //console.log("detailedExpenses"+data.detailedExpenses);
            setGroup(data.group);
            setGmDetails(data.gmDetails); 

            

            const filteredExpenses = data.detailedExpenses.filter(exp => {
                const expDate = new Date(exp.dateCreated);
                const addedDate = new Date(data.gmDetails.addedDate);
                const removedDate = data.gmDetails.removedDate ? new Date(data.gmDetails.removedDate) : new Date();
                return expDate >= addedDate && expDate <= removedDate;
            });

            console.log("filteredExpenses", JSON.stringify(filteredExpenses, null, 2));
            const sortedExpenses = filteredExpenses.sort((a, b) => -(new Date(a.dateCreated) - new Date(b.dateCreated)));
            setExpenses(data.detailedExpenses);
            const tempActiveExpenses = sortedExpenses.filter(exp => !exp.deleted);
            const tempDeletedExpenses = sortedExpenses.filter(exp => exp.deleted);
            setActiveExpenses(tempActiveExpenses);
            setDeletedExpenses(tempDeletedExpenses);
            setNewGroupName(data.groupName);
            console.log(data.deletedExpenses);
            console.log(sortedExpenses);
            console.log("active"+activeExpenses)
            console.log("expenses"+expenses);
            //console.log(data.transactions);
            setBalances(data.transactions);

            setIsLoading(false);
        } catch (error) {
            setIsLoading(false);
            if (error instanceof TypeError) {
                setConnectionError("try again later.");
            } else {
                setError(error.message);
            }
        }
    }, [groupId]);

    useEffect(() => {
        //console.log("print inside useEffect");
        fetchGroupDetails();
    }, []);

    const handleUpdateGroupName = async (event) => {
        event.preventDefault();
        setError('');
        setConnectionError('');
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

            toast.success('Group name updated successfully!');
            const updatedGroup = await response.json();
            setGroup(updatedGroup);
            setNewGroupName(updatedGroup.groupName);
            setShowUpdateForm(false); // Hide the form after successful update
            setShowExpenses(true);
            setBlurBackground(false);
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                toast.error(error.message);
            }
        }
    };

    const handleAddMember = async (event) => {
        event.preventDefault();
        setError('');
        setConnectionError('');

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
                const data = await response.text();
                console.log("coming here 2");
                throw new Error(data);
            }

            toast.success('Member added successfully!');
            //toast.success('Member added successfully!');
            //setConnectionError("Member added successfully!");
            const data = await response.json();
            console.log(data);
            setMembers(data);
            
            setNewUsername('');
            setShowAddMemberForm(false);
            //fetchGroupDetails(); 
            //console.log(members);
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Please try again later.");
            } else {
                toast.error(error.message);
            }
        }
    };

    const handleSettleGroup = async () => {

        setError('');
        setConnectionError('');
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
                if (error instanceof TypeError) {
                    setConnectionError("Unable to connect to the server. Please try again later.");
                } else {
                    setError(error.message);
                }
            }
        }
    };


    const handleDeleteGroup = async () => {
        if (window.confirm('Are you sure you want to delete this group?')) {
            setError('');
            setConnectionError('');
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
                if (error instanceof TypeError) {
                    setConnectionError("Unable to connect to the server. Please try again later.");
                } else {
                    setError(error.message);
                }
            }
        }
    };

    const handleAddExpense = async (event) => {
        event.preventDefault();

        if(payers.size === 0)
        {
           toast.error("No Payers selected");
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
            toast.error("No Participants selected. Please select at least one participant.");
            return;
        }

        let totalContributions = 0;
        payers.forEach((amount, username) => {
            totalContributions += parseFloat(amount || 0);
        });

        const totalExpense = parseFloat(newExpenseAmount);

        if (totalContributions !== totalExpense) {
            toast.error("The sum of all contributions must equal the total expense amount.");
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

        setError('');
        setConnectionError('');
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
                const data = response.text();
                throw new Error(data);
            }

            const data = await response.json();

            //console.log("data", data);
            //console.log(data.deletedExpenses);
            //setGroup(data.group);
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
            //setNewGroupName(data.groupName);
            //console.log(data.transactions);
            setBalances(data.transactions);

            toast.success('Expense added successfully!');

            setTimeout(() => {
                setNewExpenseName('');
                setNewExpenseAmount('');
                setPayers(new Map());
                setParticipants(new Map());
                setShowAddExpenseForm(false);
                fetchGroupDetails();
                setShowExpenses(true);
            }, 2000); 

        }
        catch(error)
        {
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                toast.error(error.message);
            }
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
            setError('');
            setConnectionError('');
            const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/addExpense`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify(paymentData)
            });
    
            if (!response.ok) {
                const errorData = await response.text();
                throw new Error("Failed to record payment");
            }
    
            const data = await response.json();

            // console.log(data.detailedExpenses);
            // console.log(data.transactions);

            toast.success('Payment recorded successfully!');

            setTimeout(() => {
                setShowPaymentModal(false);
            }, 2000);
            

            console.log("before filtered");

            const filteredExpenses = data.detailedExpenses.filter(exp => {
                const expDate = new Date(exp.dateCreated);
                const addedDate = new Date(data.gmDetails.addedDate);
                const removedDate = data.gmDetails.removedDate ? new Date(data.gmDetails.removedDate) : new Date();
                return expDate >= addedDate && expDate <= removedDate;
            });

            console.log("after filtered");
            const sortedExpenses = filteredExpenses.sort((a, b) => -(new Date(a.dateCreated) - new Date(b.dateCreated)));

            console.log("before sorted");
            setExpenses(sortedExpenses);
            console.log("after sorted");

            console.log("before active");
            const tempActiveExpenses = sortedExpenses.filter(exp => !exp.deleted);
            console.log("after active");

            console.log("before deleted");
            const tempDeletedExpenses = sortedExpenses.filter(exp => exp.deleted);
            console.log("ater deleted");

            console.log("before setting active");
            setActiveExpenses(tempActiveExpenses);
            console.log("after setting active");

            console.log("before setting deleted");
            setDeletedExpenses(tempDeletedExpenses);
            console.log("after setting deleted");

            setBalances(data.transactions);
            console.log("after transactions");
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Please try again later.");
            } else {
                toast.errorMessage(error.message);
            }
        }
    };


    const handleRemoveMember = async (memberUsername) => {
        if (!memberToRemove) return;

        setError('');
        setConnectionError('');

        console.log("inside 10");
        console.log(balances);
        const netBalance = balances.reduce((acc, balance) => {
            if (balance.fromUser === memberUsername) {
                return acc - balance.amount;
            } else if (balance.toUser === memberUsername) {
                return acc + balance.amount;
            }
            return acc;
        }, 0);

        console.log("netBalnce"+netBalance);

        console.log("inside 100");
        // Check if the net balance is zero
        if (netBalance !== 0) {
            //closeConfirmModal();
            if(currentUser === memberUsername)
            {
                toast.errorMessage(`Balances not settled up. Please settle all balances before leaving.`);
                return;
            }
            toast.errorMessage(`Balances not settled up for ${memberUsername}. Please settle all balances before removing.`);
            closeConfirmModal()
            return;
        }


        console.log("inside 1000");
            try {
                const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/removemember`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                    },
                    body: JSON.stringify({ username: memberToRemove })
                });

                //closeConfirmModal();
                console.log("inside 20");
                if (!response.ok) {
                    console.log("inside response");
                    throw new Error('Failed to remove member');
                }
    
                closeConfirmModal();
                toast.success(`${memberToRemove} has been removed successfully!`);
                
                // Re-fetch the group details to update the list of members
                fetchGroupDetails();
            } catch (error) {
                closeConfirmModal();
                console.log(error);
                if (error instanceof TypeError) {
                    setConnectionError("Unable to connect to the server. Please try again later.");
                } else {
                    setError(error.message);
                }
            }
    };
    
    // ConfirModal toggling functions
    const openConfirmModal = (memberUsername) => {
        setMemberToRemove(memberUsername);
        setIsConfirmModalOpen(true);
    };
    
    const closeConfirmModal = () => {
        setMemberToRemove(null);
        setIsConfirmModalOpen(false);
    };


    // Function to toggle balances visibility
    const toggleBalances = () => {
        setShowBalances(true);
        setShowAddMemberForm(false);
        setShowDeletedExpenses(false);
        setShowGroupMembers(false);
        setShowAddExpenseForm(false);
        setShowExpenses(false);
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
        setShowEditOptions(false);
    }

    const cancelAddExpense = () => {
        setShowAddExpenseForm(false);  // This will hide the form
        setShowExpenses(true);
        setPayers(new Map());
        setParticipants(new Map());
        setShowEditOptions(false);
    };

    const toggleUpdateForm = () => {

        setShowUpdateForm(true);
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
        setShowExpenses(true);
    }

    const cancelShowEditOptions = () => {
        setShowEditOptions(false);
        setBlurBackground(false);
        setShowExpenses(true);
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
    

    if (connectionError) {
        return (
          <div className={styles.errorMessage}>{connectionError}</div>
        );
    }

    if (isLoading) return <p>Loading...</p>;

    if (error) return <p>{error}</p>;


    if(showUpdateForm)
    {
        return (
            <div>
                {showUpdateForm && (
                    <form onSubmit={handleUpdateGroupName} className={styles.updateForm}>
                        <label className={styles.updateFormLabel} htmlFor="newGroupName">New Name:</label>
                        <input
                            type="text"
                            value={newGroupName}
                            onChange={(e) => setNewGroupName(e.target.value)} required
                            className={styles.updateFormInput}
                        />
                        <div className={styles.updateFormButtonGroup}>
                            <button className={styles.updateFormConfirmButton} type="submit">Submit</button>
                            <button className={styles.updateFormCancelButton} type="button" onClick={cancelUpdatingGroupName}>Cancel</button>
                        </div>   
                    </form>
                )}
                <ToastContainer position="top-center" autoClose={5000} hideProgressBar={false} newestOnTop={false} closeOnClick rtl={false} pauseOnFocusLoss draggable pauseOnHover 
                className={styles1.ToastifyToast}/>
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
                                                <label htmlFor={member.username}>{member.username}</label>
                                                <input
                                                    type="number"
                                                    id={member.username}
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
                                                <label htmlFor={member.username}>{member.username}</label>
                                                <input
                                                    type="checkbox"
                                                    id={member.username}
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
            <ToastContainer position="top-center" autoClose={5000} hideProgressBar={false} newestOnTop={false} closeOnClick rtl={false} pauseOnFocusLoss draggable pauseOnHover 
            className={styles1.ToastifyToast}/>
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
                <ToastContainer position="top-center" autoClose={5000} hideProgressBar={false} newestOnTop={false} closeOnClick rtl={false} pauseOnFocusLoss draggable pauseOnHover 
                className={styles1.ToastifyToast}/>
            </div>
        );
    }
    
    return (
        <div className={styles.background}>

    <div className={`${styles.appContainer} ${blurBackground ? styles.blurred : ''}`}>
                <NavLink to="/splitwise/logout" className={styles.logoutLink}>Logout</NavLink>
                <div className={styles.groupNameContainer}>
                    <h2>{group.groupName} {gmDetails.removedBy === null && <FaEdit className={styles.editIcon} onClick={toggleEditIconForm} />}</h2>
                    {gmDetails.removedBy !== null && gmDetails.removedDate !== null ? (
                        gmDetails.removedBy === currentUser ? (
                            <p className={styles.groupStatusRemoval}>
                                You left the group on {new Date(gmDetails.removedDate).toLocaleDateString()}
                            </p>
                        ) : (
                            <p className={styles.groupStatusRemoval}>
                                You were removed by {gmDetails.removedBy} on {new Date(gmDetails.removedDate).toLocaleDateString()}
                            </p>
                        )
                    ) : group.settledUp ? (
                        <p className={styles.groupStatusSettledUp}>Group was settled by {group.settledBy} on {new Date(group.settledDate).toLocaleDateString()}</p>
                    ) : null}
                </div>

                <div className={styles.optionsContainer}>
                    <div onClick={toggleExpenses} className={styles.optionLink}>
                        Expenses
                    </div>
                    <div onClick={toggleBalances} className={styles.optionLink}>
                        Balances
                    </div>
                    <div onClick={toggleDeletedExpenses} className={styles.optionLink}>
                        Deleted Expenses
                    </div>
                    <div onClick={toggleMembers} className={styles.optionLink}>
                        Members
                    </div>
                </div>

                {showExpenses && (
                    <div>
                    <ExpensesList 
                        group={group}
                        expenses={expenses} 
                        activeExpenses={activeExpenses}
                        groupId={groupId}
                        gmDetails={gmDetails}
                        toggleAddExpense={toggleAddExpense}
                    />
                    </div>
                )}
                
                {showDeletedExpenses && (
                    <div className={styles.deletedExpensesContainer}>
                        <h3>All Deleted Expenses</h3>
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
                    <GroupMembers 
                        members={members}
                        group={group}
                        gmDetails={gmDetails}
                        handleAddMember={handleAddMember}
                        handleRemoveMember={openConfirmModal}
                        currentUser={currentUser}
                        toggleAddMemberForm={toggleAddMemberForm}
                        newUsername={newUsername}
                        setNewUsername={setNewUsername}
                        showAddMemberForm={showAddMemberForm}
                    />
                )}

                
                {showBalances && !isLoading && (
                <Balances 
                    balances={balances}
                    gmDetails={gmDetails}
                    openPaymentModal={openPaymentModal}
                />
                )}
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

            <ConfirmModal
                isOpen={isConfirmModalOpen}
                onClose={closeConfirmModal}
                onConfirm={handleRemoveMember}
                message={`Are you sure you want to remove ${memberToRemove} from the group?`}
            />
            <ToastContainer position="top-center" autoClose={5000} hideProgressBar={false} newestOnTop={false} closeOnClick rtl={false} pauseOnFocusLoss draggable pauseOnHover 
            className={styles1.ToastifyToast}/>
        </div>
        
    );
}   

export default SplitwiseGroupDetail;
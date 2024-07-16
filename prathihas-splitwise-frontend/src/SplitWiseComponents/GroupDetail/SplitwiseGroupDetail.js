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

    const [members, setMembers] = useState(null);

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

    const [action, setAction] = useState(null);
    const [showMessage, setShowMessage] = useState(null);
    const [tempAction, setTempAction] = useState(null);

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
            setMembers(data.members);
            setGroup(data.group);
            setGmDetails(data.gmDetails); 
            
            setNewGroupName(data.groupName);
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
    }, [groupId]);

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

            const data = await response.json();
            //console.log(data);
            setMembers(data);
            
            setNewUsername('');
            setShowAddMemberForm(false);
            
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

            setGmDetails(data.gmDetails); 

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

            toast.success('Payment recorded successfully!');

            setTimeout(() => {
                setShowPaymentModal(false);
            }, 2000);

            setBalances(data.transactions);
            //console.log("after transactions");
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Please try again later.");
            } else {
                toast.error(error.message);
            }
        }
    };


    const handleRemoveMember = async () => {
        if (!memberToRemove) return;

        setError('');
        setConnectionError('');

        console.log("inside 10");
        console.log(memberToRemove);
        console.log(balances);
        const netBalance = balances.reduce((acc, balance) => {
            if (balance.fromUser === memberToRemove) {
                return acc - balance.amount;
            } else if (balance.toUser === memberToRemove) {
                return acc + balance.amount;
            }
            return acc;
        }, 0);

        console.log("netBalnce"+netBalance);

        console.log("inside 100");
        // Check if the net balance is zero
        if (netBalance !== 0) {
            //closeConfirmModal();
            if(currentUser === memberToRemove)
            {
                toast.error(`Balances not settled up. Please settle all balances before leaving.`);
                closeConfirmModal()
                return;
            }
            toast.error(`Balances not settled up for ${memberToRemove}. Please settle all balances before removing.`);
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

                //const responseMembers = await response.json();

                if(currentUser === memberToRemove)
                {
                    toast.success(`You left the group successfully!`);
                }
                else
                {
                    toast.success(`${memberToRemove} has been removed successfully!`);
                }
                
                setMembers(prevMembers => prevMembers.filter(member => member !== memberToRemove));
            } catch (error) {
                closeConfirmModal();
                console.log(error);
                if (error instanceof TypeError) {
                    setConnectionError("Unable to connect to the server. Please try again later.");
                } else {
                    toast.error(error.message);
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
    
    const handleShowPaymentModalClick = (e) => {
        e.stopPropagation();
    }

    const handleAction = (act, message) => {
        setAction(act);
        setShowMessage(message);
        setIsConfirmModalOpen(true);
    }

    const handleTempAction  = () => {
        setTempAction(action);
    }

    const setEverythingToNull = () => {
        if(tempAction === 'delete')
            toast.success('Deleted Successfully!')
        else
            toast.success('Restored Successfully!');

        setAction(null);
        setShowMessage(null);
        setTempAction(null);
    }

    if (connectionError) {
        return (
          <div className={styles.errorMessage}>{connectionError}</div>
        );
    }

    if (isLoading) return <p>Loading...</p>;

    if (error) return <p>{error}</p>;

    console.log("showMessage"+showMessage);
    console.log("action"+action);
    console.log("tempAction"+tempAction);

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

    const handleExpenseFormOverlayClick = (e) => {
        e.stopPropagation();
    };
    
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
                    {(!group.settledUp && gmDetails.removedDate === null) && (
                    <button onClick={toggleAddExpense} className={styles.addExpenseButton}>
                        Add Expense
                    </button>
                    )}
                    <ExpensesList 
                        groupId={groupId}
                        whichExpenses={true}
                        action={tempAction}
                        handleAction={handleAction}
                        setEverythingToNull={setEverythingToNull}
                    />
                    </div>
                )}
                
                {showDeletedExpenses && (
                    <div>
                        <ExpensesList 
                            groupId={groupId}
                            whichExpenses={false}
                            action={tempAction}
                            handleAction={handleAction}
                            setEverythingToNull={setEverythingToNull}
                        />
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

            {showPaymentModal && (
                <div className={styles.paymentModal} onClick={closePaymentModal}>
                <div className={styles.paymentModalContent} onClick={handleShowPaymentModalClick}>
                    <h3>{selectedBalance.fromUser} to {selectedBalance.toUser}</h3>
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
            )}

            {showAddExpenseForm && (
                <div className={styles.expenseFormOverlay} onClick={cancelAddExpense}>
                    <div className={styles.addExpenseContainer} onClick={handleExpenseFormOverlayClick}>
                        <form onSubmit={handleAddExpense}>
                            <input
                                type="text"
                                placeholder="Expense Name"
                                className={styles.inputAddExpense}
                                value={newExpenseName}
                                onChange={e => setNewExpenseName(e.target.value)}
                                required
                            />
                            <input
                                type="number"
                                placeholder="Total Amount"
                                className={styles.inputAddExpense}
                                value={newExpenseAmount}
                                onChange={e => setNewExpenseAmount(e.target.value)}
                                required
                            />

                            <div className={styles.buttonContainer}>
                                <button type="button" onClick={togglePayers} className={showPayers ? styles.activeButton : styles.inActiveButton}>
                                    Add Payers
                                </button>
                                <button type="button" onClick={toggleParticipants} className={showParticipants ? styles.activeButton : styles.inActiveButton}>
                                    Add Participants
                                </button>
                            </div>
                            

                            
                            {showPayers && (
                                    <div className={styles.participantsSection}>
                                        {members.map(memberUser => (
                                            <div key={memberUser}>
                                                <label htmlFor={memberUser} className={styles.labelForm}>
                                                {memberUser}: 
                                                <input
                                                    type="number"
                                                    id={memberUser}
                                                    value={payers.get(memberUser) || ''}
                                                    className={styles.inputForm}
                                                    onChange={e => setPayers(new Map(payers.set(memberUser, e.target.value)))}
                                                />
                                                </label>
                                            </div>
                                        ))}
                                    </div>
                            )}

                            {showParticipants && (
                            
                                    <div className={styles.participantsSection}>
                                        {members.map(memberUser => (
                                            <div key={memberUser}>
                                                <label htmlFor={memberUser} className={styles.labelForm}>
                                                <input
                                                    type="checkbox"
                                                    id={memberUser}
                                                    checked={!!participants.get(memberUser)}
                                                    onChange={e => setParticipants(new Map(participants.set(memberUser, e.target.checked)))}
                                                />
                                                {memberUser}
                                                </label>
                                            </div>
                                        ))}
                                    </div>
                                
                            )}
                            
                            <div className={styles.decisionButtonContainer}>
                                <button type="submit" className={styles.buttonSubmitAddExpense}>Done</button>
                                <button type="button" className={styles.buttonCancelAddExpense} onClick={cancelAddExpense}>
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>   
                </div>   
            )}

            {memberToRemove !== null && (<ConfirmModal
                isOpen={isConfirmModalOpen}
                onClose={closeConfirmModal}
                onConfirm={handleRemoveMember}
                message={`Are you sure you want to ${currentUser === memberToRemove ? 'leave' : 'remove ' + memberToRemove + ' from'} the group?`}
            />)}

            {showMessage !== null && (<ConfirmModal
                isOpen={isConfirmModalOpen}
                onClose={closeConfirmModal}
                onConfirm={handleTempAction}
                message={showMessage}
            />)}

            <ToastContainer position="top-center" autoClose={2000} hideProgressBar={false} newestOnTop={false} closeOnClick rtl={false} pauseOnFocusLoss={false} draggable={true} pauseOnHover={true} 
            className={styles1.ToastifyToast}/>
        </div>
        
    );
}   

export default SplitwiseGroupDetail;
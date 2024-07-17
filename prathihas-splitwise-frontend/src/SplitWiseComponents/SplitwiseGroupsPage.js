import React, { useState, useEffect } from "react";
import { NavLink, useNavigate, Link } from 'react-router-dom';
import styles from './SplitwiseGroupsPage.module.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ConfirmModal from "../Modal/ConfirmModal";
import SplitwiseCreateGroup from "./SplitwiseCreateGroup";

const SplitwiseGroupsPage = () => {
    const [allGroups, setGroups] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const [connectionError, setConnectionError] = useState('');
    

    const [activeSection, setActiveSection] = useState(null);

    const [restoring, setRestoring] = useState(null);
    const [restoreGroupId, setRestoreGroupId] = useState(null);
    const [restoreGroupName, setRestoreGroupName] = useState(null);

    const [isConfirmModalOpen, setIsConfirmModalOpen] = useState(false);

    const [showCreateGroupForm, setShowCreateGroupForm] = useState(false);


        const fetchGroups = async () => {
            const token = sessionStorage.getItem('token');
            setError('');
            setConnectionError('');
            try {
                const response = await fetch('http://localhost:8080/splitwise/groups', {
                    method: 'POST',  // Consider changing to GET if no specific data needs to be sent
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
                
                setGroups(data);
                setIsLoading(false);
            } catch (error) {
                setIsLoading(false);
                if (error instanceof TypeError) {
                    setConnectionError("Unable to connect to the server. Please try again later.");
                } else {
                    setError(error.message);
                }
            }
        };

    useEffect(() => {
        fetchGroups();
    }, [fetchGroups]);

    const handleRestoreGroup = async () => {
        try{
        const response = await fetch(`http://localhost:8080/splitwise/groups/${restoreGroupId}/restore`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            },
        });
    
        if (!response.ok) {
            const data = await response.text();
            throw new Error(data);
        } else {
            closeConfirmModal();
            toast.success(`Group ${restoreGroupName} restored successfully`);
            setGroups(allGroups.map(group => {
                if (group.id === restoreGroupId) {
                    return { ...group, deleted: false };
                }
                return group;
            }));
        }
        }catch (error) {
            closeConfirmModal();
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            }
            else{
                toast.error('Failed to restore group due to an error');
            }
        }
    };
    
    const toggleSection = (section) => {
        setActiveSection(prevSection => prevSection === section ? null : section);
    }

    const handleCreateGroup = () => {
        setShowCreateGroupForm(true);
    }

    const handleToggleRestore = (groupId, groupName) => {
        setRestoring(true);
        setRestoreGroupId(groupId);
        setRestoreGroupName(groupName);
        setIsConfirmModalOpen(true);
    }

    const closeConfirmModal = () => {
        setRestoring(false);
        setRestoreGroupId(null);
        setRestoreGroupName(null);
        setIsConfirmModalOpen(false);
        setShowCreateGroupForm(false);
    };

    const isLoggedIn = sessionStorage.getItem('token');

    if (connectionError) {
        return (
          <div className={styles.errorMessage}>{connectionError}</div>
        );
    }

    if(isLoading){
        return (<div>Loading...</div>);
    }

    console.log("restoring", restoring);

    return (
        <div className={styles.page}>
            <div className={styles.container}>
            <NavLink to="/splitwise/logout" className={styles.topRightLink}>Logout</NavLink>
            {isLoading && <p>Loading...</p>}
            {error && <div>{error}</div>}
            <button to="/splitwise/groups/creategroup" onClick={handleCreateGroup} className={`${styles.button} ${styles.topLeftButton}`}>
                Create Group
            </button>
            <h2 className={styles.activeGroupsHeader}>Active Groups</h2>
            <ul className={styles.activeGroupList}>
                {allGroups.filter(group => !group.settledUp && !group.deleted && group.removedDate === null).map(group => (
                    <li key={group.id} className={styles.activeGroupItem}>
                        <div className={styles.activeGroupName}>
                            <NavLink to={`/splitwise/groups/${group.id}`} className={styles.activeGroupLink}>
                                {group.groupName}
                            </NavLink>
                        </div>
                        <div className={styles.activeGroupDescription}>
                            {group.groupDescription}
                        </div>
                    </li>
                ))}
            </ul>

            {showCreateGroupForm === true && (
                <SplitwiseCreateGroup 
                    setShowCreateGroupForm={setShowCreateGroupForm}
                    closeConfirmModal={closeConfirmModal}
                    fetchGroups={fetchGroups}
                    setGroups={setGroups}
                />
            )
            }
            <div className={styles.toggleContainer}>
                <h2 className={`${styles.toggleButton} ${activeSection === 'settled' ? styles.toggleButtonActive : ''}`} onClick={() => toggleSection('settled')}>
                {activeSection === 'settled' ? 'Hide Settled Groups' : 'Show Settled Groups'}
                </h2>

                <h2 className={`${styles.toggleButton} ${activeSection === 'deleted' ? styles.toggleButtonActive : ''}`} 
             onClick={() => toggleSection('deleted')}>{activeSection === 'deleted' ? 'Hide Deleted Groups' : 'Show Deleted Groups'}</h2>
            </div>


            {activeSection === 'settled' && (
                <ul className={styles.groupList}>
                    {allGroups.filter(group => (group.settledUp && !group.deleted) || (group.removedDate !== null)).map(group => (
                        <li key={group.id} className={styles.groupItem}>
                            <NavLink to={`/splitwise/groups/${group.id}`} className={styles.navLink}>
                                {group.groupName}
                            </NavLink>  {group.groupDescription}
                        </li>
                    ))}
                </ul>
            )}

            {activeSection === 'deleted' && (
                <ul className={styles.groupList}>
                    {allGroups.filter(group => group.deleted && group.removedDate === null).map(group => (
                        <li key={group.id} className={`${styles.groupItem} ${styles.groupItemDeleted}`}>
                            <NavLink to={`/splitwise/groups/${group.id}`} className={styles.navLink}>
                                {group.groupName}
                            </NavLink>  {group.groupDescription}
                            <button onClick={() => handleToggleRestore(group.id, group.groupName)} className={styles.button}>Restore</button>
                        </li>
                    ))}
                </ul>
            )}

            {restoring === true && (<ConfirmModal
                isOpen={isConfirmModalOpen}
                onClose={closeConfirmModal}
                onConfirm={handleRestoreGroup}
                message={`Are you sure you want to restore this group?`}
            />)}

            <ToastContainer position="top-center" autoClose={2000} hideProgressBar={false} newestOnTop={false} closeOnClick rtl={false} pauseOnFocusLoss={false} draggable={true} pauseOnHover={true} />
        </div>
        </div> 
    );
    
}

export default SplitwiseGroupsPage;

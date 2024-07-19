import React, { useState, useEffect, useCallback} from "react";
import { NavLink, useNavigate, Link } from 'react-router-dom';
import styles from './SplitwiseGroupsPage.module.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ConfirmModal from "../Modal/ConfirmModal";
import SplitwiseCreateGroup from "./SplitwiseCreateGroup";

const SplitwiseGroupsPage = () => {

    const navigate = useNavigate();

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

                const response = await fetch('http://52.15.44.104:8080/splitwise/groups', {
                    method: 'POST',  
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                });   

                if (response.status === 403) {
                    setConnectionError("You do not have permission to access this resource. Redirecting to logout...");
                    setTimeout(() => navigate('/prathihas-splitwise/logout'), 5000);
                    return;
                }
                else
                {

                if (!response.ok) {
                    const data = await response.text();
                    throw new Error(data);
                }

                const data = await response.json();
                
                setGroups(data);
                setIsLoading(false);
                
                }
            } catch (error) {
                setIsLoading(false);
                if (error instanceof TypeError) {
                    setConnectionError("Unable to connect to the server. Please try again later.");
                    setTimeout(() => navigate('/prathihas-splitwise/logout'), 5000); 
                    return;  
                } else {
                    setConnectionError(error.message);
                }
            }
    };

    useEffect(() => {
        fetchGroups();
    }, []);

    const handleRestoreGroup = async () => {
        try{
        const response = await fetch(`http://52.15.44.104:8080/splitwise/groups/${restoreGroupId}/restore`, {
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
               setTimeout(() => navigate('/prathihas-splitwise/logout'), 5000);    
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

    if (connectionError) {
        return (
          <div className={styles.errorMessage}>{connectionError}</div>
        );
    }

    if(isLoading){
        return (
            <div className={styles.loaderContainer}>
                <div className={styles.loader}></div>
            </div>
        );
    }

    const activeGroups = allGroups.filter(group => !group.settledUp && !group.deleted && group.removedDate === null);

    return (
        <div className={styles.page}>
            <div className={styles.container}>
            <NavLink to="/prathihas-splitwise/logout" className={styles.topRightLink}>Logout</NavLink>
            {isLoading && <p>Loading...</p>}
            {error && <div>{error}</div>}
            {activeGroups.length > 0 && (<button to="/prathihas-splitwise/groups/creategroup" onClick={handleCreateGroup} className={`${styles.button} ${styles.topLeftButton} ${styles.pulseButton}`}>
                Create Group
            </button>)}
            <h1 className={styles.activeGroupsHeader}>Active Groups</h1>
            <ul className={styles.activeGroupList}>
                {allGroups.filter(group => !group.settledUp && !group.deleted && group.removedDate === null).map(group => (
                    <li key={group.id} className={styles.activeGroupItem}>
                        <div className={styles.activeGroupName}>
                            <NavLink to={`/prathihas-splitwise/groups/${group.id}`} className={styles.activeGroupLink}>
                                {group.groupName}
                            </NavLink>
                        </div>
                        <div className={styles.activeGroupDescription}>
                            {group.groupDescription}
                        </div>
                    </li>
                ))}

                {activeGroups.length === 0 && (
                    <div className={styles.activeGroupsHeader}>
                            <h1>There are no active groups!</h1>
                            <h2>You can create a group by clicking on create group button!</h2>
                            <div className={styles.arrow}></div>
                        <button to="/prathihas-splitwise/groups/creategroup" onClick={handleCreateGroup} className={`${styles.notActiveButton} ${styles.pulseButton}`}>
                                Create Group
                        </button>
                    </div> 
                )}
            </ul>

            {showCreateGroupForm === true && (
                <SplitwiseCreateGroup 
                    setShowCreateGroupForm={setShowCreateGroupForm}
                    closeConfirmModal={closeConfirmModal}
                    fetchGroups={fetchGroups}
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
                            <NavLink to={`/prathihas-splitwise/groups/${group.id}`} className={styles.navLink}>
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
                            <NavLink to={`/prathihas-splitwise/groups/${group.id}`} className={styles.navLink}>
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

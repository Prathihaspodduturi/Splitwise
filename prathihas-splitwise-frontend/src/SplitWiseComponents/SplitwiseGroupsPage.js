import React, { useState, useEffect } from "react";
import { NavLink, useNavigate, Link } from 'react-router-dom';
import styles from './SplitwiseGroupsPage.module.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const SplitwiseGroupsPage = () => {
    const [allGroups, setGroups] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const [connectionError, setConnectionError] = useState('');
    

    const [activeSection, setActiveSection] = useState(null);

    const navigate = useNavigate();

    useEffect(() => {
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

        fetchGroups();
    }, []);

    const handleRestoreGroup = async (groupId, groupName) => {
        try{
        const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/restore`, {
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
            toast.success(`Group ${groupName} restored successfully`);
            setGroups(allGroups.map(group => {
                if (group.id === groupId) {
                    return { ...group, deleted: false };
                }
                return group;
            }));
        }
        }catch (error) {
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
        navigate("/splitwise/groups/creategroup");
    }

    const isLoggedIn = sessionStorage.getItem('token');

    if (connectionError) {
        return (
          <div className={styles.errorMessage}>{connectionError}</div>
        );
    }

    if(isLoading){
        return (<div>Loading...</div>);
    }

    return (
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
                            {group.groupName} - {group.groupDescription}
                            <button onClick={() => handleRestoreGroup(group.id, group.groupName)} className={styles.button}>Restore</button>
                        </li>
                    ))}
                </ul>
            )}
            <ToastContainer position="top-center" autoClose={5000} hideProgressBar={false} newestOnTop={false} closeOnClick rtl={false} pauseOnFocusLoss draggable pauseOnHover />
        </div>
    );
    
}

export default SplitwiseGroupsPage;

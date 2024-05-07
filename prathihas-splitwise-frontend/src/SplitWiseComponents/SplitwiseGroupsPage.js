import React, { useState, useEffect } from "react";
import { NavLink, useNavigate, Link } from 'react-router-dom';
import styles from './SplitwiseGroupsPage.module.css';


const SplitwiseGroupsPage = () => {
    const [allGroups, setGroups] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showCreateGroupForm, setShowCreateGroupForm] = useState(false);
    const [groupName, setGroupName] = useState('');
    const [groupDescription, setGroupDescription] = useState('');
    const [showSettledGroups, setShowSettledGroups] = useState(false);
    const [showDeletedGroups, setShowDeletedGroups] = useState(false);

    const [activeSection, setActiveSection] = useState(null);


    useEffect(() => {
        const fetchGroups = async () => {
            const token = sessionStorage.getItem('token');
            try {
                const response = await fetch('http://localhost:8080/splitwise/groups', {
                    method: 'POST',  // Consider changing to GET if no specific data needs to be sent
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                });

                if (!response.ok) {
                    console.log(response);
                    throw new Error('Something went wrong!');
                }

                const data = await response.json();
                console.log("data", data);
                setGroups(data);
                setIsLoading(false);
            } catch (error) {
                setError(error.message);
                setIsLoading(false);
            }
        };

        fetchGroups();
    }, []);

    const handleToggleCreateGroup = () => {
        setShowCreateGroupForm(!showCreateGroupForm);
    };

    const handleCreateGroup = async (event) => {
        event.preventDefault();
        const token = sessionStorage.getItem('token');
        try {
            const response = await fetch("http://localhost:8080/splitwise/creategroup", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ groupName, groupDescription })
            });

            if (!response.ok) {
                throw new Error('Failed to create group');
            }

            const data = await response.json(); // Assuming it includes some kind of id or confirmation
            alert("Group created successfully");
            setShowCreateGroupForm(false);
            setGroupName('');
            setGroupDescription('');
            setGroups([...allGroups, data]); // Optionally add the new group to the list without re-fetching
        } catch (error) {
            setError(error.message);
        }
    };

    const handleRestoreGroup = async (groupId) => {
        const response = await fetch(`http://localhost:8080/splitwise/groups/${groupId}/restore`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            },
        });
    
        if (!response.ok) {
            alert('Failed to restore group');
        } else {
            alert('Group restored successfully');
            setGroups(allGroups.map(group => {
                if (group.id === groupId) {
                    return { ...group, deleted: false };
                }
                return group;
            }));
        }
    };
    
    const toggleSection = (section) => {
        setActiveSection(prevSection => prevSection === section ? null : section);
    }

    const isLoggedIn = sessionStorage.getItem('token');

    if (showCreateGroupForm) {
        return (
            <div className={styles.container}>
                <form onSubmit={handleCreateGroup} className={styles.form}>
                    <div>
                        <label htmlFor="groupName" className={styles.formLabel}>Group Name:</label>
                        <input
                            id="groupName"
                            type="text"
                            value={groupName}
                            onChange={(e) => setGroupName(e.target.value)}
                            className={styles.formInput}
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="groupDescription" className={styles.formLabel}>Group Description:</label>
                        <textarea
                            id="groupDescription"
                            value={groupDescription}
                            onChange={(e) => setGroupDescription(e.target.value)}
                            className={styles.formTextArea}
                        />
                    </div>
                    <button type="submit" className={styles.button}>Submit</button>
                    <button type="button" className={styles.button} onClick={handleToggleCreateGroup}>Cancel</button>
                </form>
            </div>
        );
    }


    return (
        <div className={styles.container}>
            <NavLink to="/splitwise/logout" className={styles.topRightLink}>Logout</NavLink>
            {isLoading && <p>Loading...</p>}
            {error && <p>Error: {error}</p>}
            <button onClick={handleToggleCreateGroup} className={`${styles.button} ${styles.topLeftButton} ${showCreateGroupForm ? styles.buttonCancel : ''}`}>
                {showCreateGroupForm ? 'Cancel Creation' : 'Create Group'}
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
                            <button onClick={() => handleRestoreGroup(group.id)} className={styles.button}>Restore</button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
    
}

export default SplitwiseGroupsPage;

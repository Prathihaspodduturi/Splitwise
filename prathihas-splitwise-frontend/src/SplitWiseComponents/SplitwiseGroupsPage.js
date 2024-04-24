import React, { useState, useEffect } from "react";
import { NavLink, useNavigate, Link } from 'react-router-dom';

const SplitwiseGroupsPage = () => {
    const [allGroups, setGroups] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showCreateGroupForm, setShowCreateGroupForm] = useState(false);
    const [groupName, setGroupName] = useState('');
    const [groupDescription, setGroupDescription] = useState('');
    const [showSettledGroups, setShowSettledGroups] = useState(false);
    const [showDeletedGroups, setShowDeletedGroups] = useState(false);


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
                    throw new Error('Something went wrong!');
                }

                const data = await response.json();
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
    


    const isLoggedIn = sessionStorage.getItem('token');

    return (
        <div>
            <NavLink to="/splitwise/logout">Logout</NavLink>
            <h1>Groups</h1>
            {isLoading && <p>Loading...</p>}
            {error && <p>Error: {error}</p>}
            <button onClick={handleToggleCreateGroup}>
                {showCreateGroupForm ? 'Cancel Create Group' : 'Create Group'}
            </button>
            {showCreateGroupForm && (
                <form onSubmit={handleCreateGroup}>
                    <div>
                        <label htmlFor="groupName">Group Name:</label>
                        <input
                            id="groupName"
                            type="text"
                            value={groupName}
                            onChange={(e) => setGroupName(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="groupDescription">Group Description:</label>
                        <textarea
                            id="groupDescription"
                            value={groupDescription}
                            onChange={(e) => setGroupDescription(e.target.value)}
                        />
                    </div>
                    <button type="submit">Submit</button>
                </form>
            )}

            <h2>Active Groups</h2>
            <ul>
                    {allGroups.filter(group => !group.settledUp && !group.deleted).map(group => (
                        <li key={group.id}>
                            <NavLink to={`/splitwise/groups/${group.id}`}>
                                {group.groupName}
                            </NavLink> - {group.groupDescription}
                        </li>
                    ))}
            </ul>
    
            <h2 onClick={() => setShowSettledGroups(!showSettledGroups)}>Settled Groups</h2>
            {showSettledGroups && (
                <ul>
                    {allGroups.filter(group => group.settledUp && !group.deleted).map(group => (
                        <li key={group.id}>
                            <NavLink to={`/splitwise/groups/details/${group.id}`}>
                                {group.groupName}
                            </NavLink> - {group.groupDescription}
                        </li>
                    ))}
                </ul>
            )}
    
            <h2 onClick={() => setShowDeletedGroups(!showDeletedGroups)}>Deleted Groups</h2>
            {showDeletedGroups && (
                <ul>
                    {allGroups.filter(group => group.deleted).map(group => (
                        <li key={group.id} style={{ color: 'red' }}>
                            {group.groupName} - {group.groupDescription}
                            <button onClick={() => handleRestoreGroup(group.id)}>Restore</button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
    
}

export default SplitwiseGroupsPage;

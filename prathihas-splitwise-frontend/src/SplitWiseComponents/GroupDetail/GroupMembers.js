import React from 'react';
import styles from './GroupMembers.module.css';

const GroupMembers = ({members, group, gmDetails, handleAddMember, handleRemoveMember, currentUser, toggleAddMemberForm, newUsername, setNewUsername, showAddMemberForm} ) => {

    const toggleCancelButton = () => {
        setNewUsername('');
        toggleAddMemberForm(false);
    }

    return (
        
        <div>
            <div className={styles.membersContainer}>
                        <h3 className={styles.username}>Group Members</h3>
                        <ul>
                            {members.map(username => ( 
                                <li key={username} className={styles.membersItem}>
                                    <span className={styles.username}>{username}</span>
                                    {(!group.settledUp) && (!group.deletedBy) && (gmDetails.removedDate === null) && (
                                        currentUser === username ? 
                                            <button onClick={() => handleRemoveMember(username)} className={styles.removeMemberButton}>Leave Group</button>
                                            :
                                            <button onClick={() => handleRemoveMember(username)} className={styles.removeMemberButton}>
                                                Remove
                                            </button>
                                    )}
                                </li>
                            ))}
                        </ul>

                        {!group.settledUp && !group.deletedDate && <div onClick={toggleAddMemberForm} className={styles.addMemberButton} >
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
                                    <button type="button" onClick={toggleCancelButton} className={styles.addMemberFormButton}>Cancel</button>
                                </div>
                            </form>
                        )}
                    </div>

        </div>
        
    )
}

export default GroupMembers;
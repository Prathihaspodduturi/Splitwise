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
                                    <button type="button" onClick={toggleCancelButton} className={styles.addMemberFormButton}>Cancel</button>
                                </div>
                            </form>
                        )}
                    </div>

        </div>
        
    )
}

export default GroupMembers;
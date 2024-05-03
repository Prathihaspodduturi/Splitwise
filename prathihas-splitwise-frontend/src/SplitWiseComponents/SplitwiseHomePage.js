import React from "react";
import { NavLink } from 'react-router-dom';
import styles from './SplitwiseHomePage.module.css'; // Importing CSS module


const SplitwiseHomePage = () => {
    const isLoggedIn = sessionStorage.getItem('token');

    return (
        <div className={styles.container}>
            {isLoggedIn ? (
                <>
                    <NavLink to="/splitwise/groups" className={styles.navLinks}>Manage Groups</NavLink>
                    <NavLink to="/splitwise/logout" className={styles.navLinks} onClick={() => { sessionStorage.clear(); }}>Logout</NavLink>
                </>
            ) : (
                <div>
                    <h1 className={styles.title}>Welcome to My Splitwise</h1>
                    <p>A simple site to split and maintain expenses</p>
                    <NavLink to="/splitwise/login" className={styles.navLinks}>Login</NavLink>
                    <NavLink to="/splitwise/signup" className={styles.navLinks}>Sign Up</NavLink>
                </div>
            )}
        </div>
    );
}

export default SplitwiseHomePage;

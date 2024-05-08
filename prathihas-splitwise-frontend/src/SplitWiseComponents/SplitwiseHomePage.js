import React from "react";
import { useEffect } from "react";
import { NavLink, useNavigate } from 'react-router-dom';
import styles from './SplitwiseHomePage.module.css'; // Importing CSS module


const SplitwiseHomePage = () => {
    const navigate = useNavigate();
    const isLoggedIn = sessionStorage.getItem('token');

    useEffect(() => {
        if (isLoggedIn) {
            navigate('/splitwise/groups');  // Navigate to the groups page if logged in
        }
    }, [isLoggedIn, navigate]);

    return (
        <div className={styles.container}>
                <div>
                    <h1 className={styles.title}>Welcome to My Splitwise</h1>
                    <p>A simple site to split and maintain expenses</p>
                    <NavLink to="/splitwise/login" className={styles.navLinks}>Login</NavLink>
                    <NavLink to="/splitwise/signup" className={styles.navLinks}>Sign Up</NavLink>
                </div>
        </div>
    );
}

export default SplitwiseHomePage;

import React from "react";
import { useEffect, useState } from "react";
import { NavLink, useNavigate } from 'react-router-dom';
import styles from './SplitwiseHomePage.module.css'; // Importing CSS module


const SplitwiseHomePage = () => {
    const navigate = useNavigate();
    const isLoggedIn = sessionStorage.getItem('token');
    const [connectionError, setConnectionError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (isLoggedIn) {
            navigate('/prathihas-splitwise/groups');  // Navigate to the groups page if logged in
        }
    }, [isLoggedIn, navigate]);

    const fetchConnection = async() => {
        setConnectionError('');
        try
        {
            const response = await fetch("/splitwise/");

            if(!response.ok)
            {
                throw new Error(`Could not connect. Try again later`);
            }
            setConnectionError('');
        }
        catch(Error)
        {
            setConnectionError("Unable to connect to the server. Please try again later.");
        }
        finally
        {
            setLoading(false);
        }
    }

    useEffect(() => { 
        fetchConnection();
    },[]);

    if (connectionError) {
        return (
          <div className={styles.errorMessage}>{connectionError}</div>
        );
    }

    if(loading)
    {
        return (
            <div>Loading...</div>
        );
    }

    return (
        <div className={styles.container}>
                <div>
                    <h1 className={styles.title}>Welcome to Splitwise</h1>
                    <p>A simple site to split and maintain expenses</p>
                    <NavLink to="/prathihas-splitwise/login" className={styles.navLinks}>Login</NavLink>
                    <NavLink to="/prathihas-splitwise/signup" className={styles.navLinks}>Sign Up</NavLink>
                </div>
        </div>
    );
}

export default SplitwiseHomePage;

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './SplitwiseLoginPage.module.css';

const SplitwiseSignupPage = () => {
    const navigate = useNavigate();

    useEffect(() => {
      const token = sessionStorage.getItem('token'); 

      if(token) {
        navigate('/splitwise/groups');
      }
    }, []);

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [signUpMessage, setSignUpMessage] = useState('');
    const [connectionError, setConnectionError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        setSignUpMessage('');
        setConnectionError('');
        try {
            const response = await fetch('http://localhost:8080/splitwise/signup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            const data = await response.text();
            if (!response.ok) {
                throw new Error(data);
            }
            setUsername('');
            setPassword('');
            setSignUpMessage('Signup successfull');
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                setErrorMessage(error.message);
            }
        }
    };

    const handleLoginRedirect = () => {
        navigate('/splitwise/login'); 
    };

    if (connectionError) {
        return (
          <div className={styles.errorMessage}>{connectionError}</div>
        );
    }

    return (
        <div className={styles.page}>
            <div className={styles.container}>
            {errorMessage && <div className={styles.error}>{errorMessage}</div>}
            {signUpMessage && <div className={styles.signUpMessage}>{signUpMessage}</div>}
            <h2 className={styles.heading}>Sign Up</h2>
            <form onSubmit={handleSubmit}>
                <div className={styles.formGroup}>
                    <label htmlFor="username" className={styles.label}>Username:</label>
                    <input id="username" className={styles.input} type="text" value={username} onChange={e => setUsername(e.target.value)} required />
                </div>
                <div className={styles.formGroup}>
                    <label htmlFor="password" className={styles.label}>Password:</label>
                    <input id="password" className={styles.input} type="password" value={password} onChange={e => setPassword(e.target.value)} required />
                </div>
                <button className={styles.button} type="submit">Sign Up</button>
            </form>
            <div>
                <p className={styles.para}>Already a user: <span onClick={handleLoginRedirect} className={styles.promptLink}>Login Here</span></p> 
            </div>
        </div>
        </div>
        
    );
};

export default SplitwiseSignupPage;

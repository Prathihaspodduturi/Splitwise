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

    const [isLoading, setIsLoading] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        setSignUpMessage('');
        setConnectionError('');
        setIsLoading(true);

        const usernameRegex = /^[a-zA-Z0-9_]{5,15}$/;
        if (!usernameRegex.test(username)) {

            setTimeout(() => {
            setIsLoading(false);
            setErrorMessage('Username must be between 5 and 15 characters long and can only contain letters, numbers, and underscores.');
            return;
            }, 1000);
        }
        else
        {
        // Password validation
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
        if (!passwordRegex.test(password)) {

            setTimeout(() => {
            setIsLoading(false);
            setErrorMessage('Password must contain at least one symbol, one number, one uppercase letter, one lowercase letter, and be at least 8 characters long.');
            return;
            }, 1000);
        }
        else
        {
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
                setIsLoading(false);
                setSignUpMessage('Signup successfull');
            } catch (error) {
                setIsLoading(false);
                if (error instanceof TypeError) {
                    setConnectionError("Unable to connect to the server. Please try again later.");    
                } else {
                    setErrorMessage(error.message);
                }
            }
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
            {isLoading && (
                    <div className={styles.loaderContainer}>
                        <div className={styles.loader}></div>
                    </div>
            )}
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

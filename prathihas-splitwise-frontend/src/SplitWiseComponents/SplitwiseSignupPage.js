import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './SplitwiseSignupPage.module.css';

const SplitwiseSignupPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [signUpMessage, setSignUpMessage] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        setSignUpMessage('');
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
            setSignUpMessage('Signup successful');
        } catch (error) {
            if (error instanceof TypeError) {
                setErrorMessage("Unable to connect to the server. Please try again later.");
            } else {
                setErrorMessage(error.message);
            }
        }
    };

    const handleLoginRedirect = () => {
        navigate('/splitwise/login'); 
    };

    return (
        <div className={styles.container}>
            {errorMessage && <div className={styles.errorMessage}>{errorMessage}</div>}
            {signUpMessage && <div className={styles.signUpMessage}>{signUpMessage}</div>}
            <h2>Sign Up</h2>
            <form onSubmit={handleSubmit}>
                <div className={styles.formGroup}>
                    <label className={styles.label}>Username:</label>
                    <input className={styles.input} type="text" value={username} onChange={e => setUsername(e.target.value)} required />
                </div>
                <div className={styles.formGroup}>
                    <label className={styles.label}>Password:</label>
                    <input className={styles.input} type="password" value={password} onChange={e => setPassword(e.target.value)} required />
                </div>
                <button className={styles.button} type="submit">Sign Up</button>
            </form>
            <div className={styles.link} onClick={handleLoginRedirect}>
                Already a user? Login here
            </div>
        </div>
    );
};

export default SplitwiseSignupPage;

import React from "react";

import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from './SplitwiseLoginPage.module.css';

const SplitwiseLoginPage  = () => {

  const navigate = useNavigate();

  useEffect(() => {
    const token = sessionStorage.getItem('token'); 

    if(token) {
      navigate('/prathihas-splitwise/groups');
    }
  }, []);
    
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isSubmitted, setIsSubmiitted] = useState(false);
    const [error, setError] = useState('');
    const [connectionError, setConnectionError] = useState('');  

    const isLoggedIn = sessionStorage.getItem('LoggedIn');

    const handleSubmit = async(e) =>
    {
        e.preventDefault();
        setIsSubmiitted(true);
        setError('');
        setConnectionError('');
          try{
  
          const response = await fetch("http://52.15.44.104:8080/splitwise/login", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json'},
            body: JSON.stringify({ "username" : username, "password" : password })
          });
          
          const data = await response.text();

          if(!response.ok){
            throw new Error(data);
          }

          const jwtToken = data;
          sessionStorage.setItem('token', jwtToken);
          sessionStorage.setItem('Connected', true);
          sessionStorage.setItem('username', username);
          navigate('/prathihas-splitwise/groups');
          
          setError('');
        }
        catch(error)
        {
          if (error.name === "TypeError" || error.message === "Failed to fetch") {
            setConnectionError("Unable to connect to the server. Please try again later.");
        } else {
            setIsSubmiitted(false);
            setError(error.message);
          }
        }
        setIsSubmiitted(false);
    }

    const handleSignUpReDirect = () => {
      navigate('/prathihas-splitwise/signup');
    }

    if (connectionError) {
      return (
        <div className={styles.errorMessage}>{connectionError}</div>
      );
    }

    return (
      <div className={styles.page}>
        <div className={styles.container}>
          {connectionError && (<div className={styles.error}>{connectionError}</div>)}
          {error && <div className={styles.error}>{error}</div>}
          {!connectionError && (
              <>
                  <h1 className={styles.heading}>Please login to your account</h1>
                  <form onSubmit={handleSubmit}>
                      <div className={styles.formGroup}>
                          <label htmlFor="username" className={styles.label}>Username:</label>
                          <input type="text"
                                 id="username"
                                 value={username}
                                 onChange={(e) => setUsername(e.target.value)} required
                                 className={styles.input} />
                      </div>
                      <div className={styles.formGroup}>
                          <label htmlFor="password" className={styles.label}>Password:</label>
                          <input type="password"
                                 id="password"
                                 value={password}
                                 onChange={(e) => setPassword(e.target.value)} required
                                 className={styles.input} />
                      </div>
                      <button type="submit" className={styles.button}>Log In</button>
                      <div className={styles.prompt}>
                          <p className={styles.para}>Don't have an account? <span className={styles.promptLink} onClick={handleSignUpReDirect}>Sign Up</span></p>
                      </div>
                  </form>
              </>
          )}
      </div>
      </div>
      
    );
};


export default SplitwiseLoginPage;



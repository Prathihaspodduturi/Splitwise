import React from "react";

import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import SplitwiseHomePage from "./SplitwiseHomePage";
import styles from './SplitwiseLoginPage.module.css';

const SplitwiseLoginPage  = () => {

  const navigate = useNavigate();

  useEffect(() => {
    const token = sessionStorage.getItem('token'); 

    if(token) {
      navigate('/splitwise/');
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
          try{
  
          const response = await fetch("http://localhost:8080/splitwise/login", {
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
          //sessionStorage.setItem('LoggedIn', true);
          //console.log("token is"+sessionStorage.getItem('token'));
          navigate('/splitwise/');
          
          setError('');
        }
        catch(error)
        {
          //console.log(error.name);
          //console.log(error.message);
          if (error.name === "TypeError" || error.message === "Failed to fetch") {
            //console.log("inside");
            setConnectionError("Unable to connect to the server. Please try again later.");
            //console.log(connectionError);
        } else {
            // Handle other errors
            setIsSubmiitted(false);
            setError(error.message);
          }
        }
        setIsSubmiitted(false);
    }


    const handleSignUpReDirect = () => {
      navigate('/splitwise/signup');
    }

    return (
      <div className={styles.container}>
          {connectionError && (<div className={styles.error}>{connectionError}</div>)}
          {error && <div className={styles.error}>{error}</div>}
          {!connectionError && (
              <>
                  <h1>Please login to your account</h1>
                  <form onSubmit={handleSubmit}>
                      <div className={styles.formGroup}>
                          <label htmlFor="username" className={styles.label}>Username</label>
                          <input type="text"
                                 id="username"
                                 value={username}
                                 onChange={(e) => setUsername(e.target.value)}
                                 required
                                 className={styles.input} />
                      </div>
                      <div className={styles.formGroup}>
                          <label htmlFor="password" className={styles.label}>Password</label>
                          <input type="password"
                                 id="password"
                                 value={password}
                                 onChange={(e) => setPassword(e.target.value)}
                                 required
                                 className={styles.input} />
                      </div>
                      <button type="submit" className={styles.button}>Log In</button>
                      <div className={styles.signUpPrompt}>
                          <p>Don't have an account? <span className={styles.signUpLink} onClick={handleSignUpReDirect}>Sign Up</span></p>
                      </div>
                  </form>
              </>
          )}
      </div>
    );
};


export default SplitwiseLoginPage;



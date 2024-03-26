import React, {useState, useEffect} from 'react';
import { useNavigate } from 'react-router-dom';

const SplitwiseSignupPage = () =>
{
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const [errorMessage, setErrorMessage] = useState('');
    const [connectionError, setConnectionError] = useState('');
    const [signUpMessage, setSignUpMessage] = useState('');

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        //setConnectionError('');
        setSignUpMessage('');
        // Perform the signup request
        try {
            const response = await fetch('http://localhost:8080/signup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ "username": username, "password": password })
            });

            const data = await response.text();

            if (!response.ok) {
                throw new Error(data);
            }
            setUsername('');
            setPassword('');
            setSignUpMessage('signup successfull');
        } catch (error) {
            if (error instanceof TypeError) {
                setConnectionError("Unable to connect to the server. Please try again later.");
            } else {
                // Handle other errors
                //alert('error');
                setPassword('');
                setErrorMessage(error.message);
              }
        }
    };

    const handleLoginRedirect = () => {
        navigate('/splitwise-login'); 
    };

    return (
        <div>
            {connectionError && (<div>{connectionError}</div>)}
            {!connectionError && (
                <div>
                    <h2>Sign Up</h2>
                    <form onSubmit={handleSubmit}>
                        <div>
                            <label>Username:</label>
                            <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} required />
                        </div>
                        <div>
                            <label>Password:</label>
                            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required/>
                        </div>
                        <button type="submit">Sign Up</button>
                    </form>
                    <div>
                        <p>Already a user: <span onClick={handleLoginRedirect}>Login</span></p> 
                    </div>
                    {errorMessage && <p>{errorMessage}</p>}
                    {signUpMessage && <p>{signUpMessage}</p>}
                </div>
            )}
        </div>
    );
};

export default SplitwiseSignupPage;
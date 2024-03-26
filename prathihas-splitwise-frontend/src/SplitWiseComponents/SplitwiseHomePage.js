import React, {useState, useEffect}  from "react";
import {NavLink, useLocation, useNavigate} from 'react-router-dom';

const SplitwiseHomePage = () => {

  const [initialConnection, setConnection] = useState(false);
  const [error,setError] = useState(false);
  const [flag, setFlag] = useState(false);
  const [connectionError, setConnectionError] = useState('');
  const [url, setUrl] = useState('');
  const [shortUrl, setShortUrl] = useState('');
  const [urlExistsError, setUrlExistsError] = useState('');

  const [isToken, setIsToken] = useState(false);

  const LoggedIn = sessionStorage.getItem('LoggedIn');

    useEffect(() => {
      const token = sessionStorage.getItem('token'); 

      if(token) {
        setIsToken(true); 
      }
    }, []);

    useEffect(() => {

      const fetchConnection = async() => {
        setConnectionError('');
        try{

          if(initialConnection === true)
          {
            console.log("true");
            return;
          }

          console.log("connecting");

        const response = await fetch("http://localhost:8080/");
        if(!response.ok){
          throw new Error(`Could not connect. Try again later`);
        }

        const data = await response.text();
        setFlag(true);
        setConnection(true);
        sessionStorage.setItem("Connected", "true");
        setConnection(initialConnection);
        
      }
      catch(Error)
      {
        if(Error instanceof TypeError)
        {
          setConnectionError("Unable to connect to server. Please Try again later!");
        }
        else{
        setError(true);
        setFlag(true);

        console.log(error);
        }
      }
    };
  
    if(!initialConnection){
      const timerId = setTimeout(fetchConnection, 150);
      return () => clearTimeout(timerId);
    }
    else
    {
      setFlag(true);
    }
  },[initialConnection]);
  
  
    

    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        setShortUrl('');
        setConnectionError('');
        //setError('');
        try
        {
            const token = sessionStorage.getItem('token');
            const response = await fetch("http://localhost:8080/saveUrl",{
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({"originalUrl": url})
            });

          if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(errorMessage);
          }
          const data = await response.text();
          setError('');
          setShortUrl(data);
          console.log("receieved data is " + data);

          setUrl('');
        } catch (error) {
          if (error instanceof TypeError) {
            sessionStorage.clear();
            setConnectionError('Unable to connect to the server. Click on refresh to redirect to loginpage');
            setTimeout(() => {
              navigate('/splitwise-home', { state: { error: "Unable to connect to the server. Please check your connection and try again." } });
          });
        } else {
            setError(error.message);
          }
        }
    }

  return (
    <div>
      <div>
        {isToken && (
          <>
            <NavLink to="/myurls">My URLs</NavLink>
            <NavLink to="/logout" onClick={() => {sessionStorage.removeItem('token'); setIsToken(false);}}>Logout</NavLink>
          </>
        ) 
        }
      </div>
      <div>
        <h1>Welcome to My Splitwise</h1>
        <p>A simple site to split ans maintain expenses</p>
        {isToken && (
          <form onSubmit={handleSubmit}>
          <label htmlFor="url">Enter The Original URL:</label>
          <input
            type="text"
            id="url"
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            required
          />
          <button type="submit">Submit</button>
        </form>
        )}
        {shortUrl && (
          <div>
          <p>Shortened URL:</p>
          <a href={shortUrl} target="_blank" rel="noopener noreferrer">
          {shortUrl}
          </a>
          </div>
        )}
        {error && (<div>{error}</div>)}
        {!isToken && (
          <div>
            <p>Please log in or sign up to use the splitwise app.</p>
          </div>
        )}
        {!isToken && (
        <div>
          <NavLink to="/splitwise-login">Login</NavLink>
          <NavLink to="/splitwise-signup">Sign Up</NavLink>
        </div>
        )}
      </div>
    </div>
  );
  
}

export default SplitwiseHomePage;
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const SplitwiseLogout = () => 
{
    const navigate = useNavigate();

    useEffect(() => {
        //console.log("Clearing session storage");
        sessionStorage.clear();

        //console.log("Navigating to /splitwise/");
        setTimeout(() => navigate('/splitwise/'), 1000);
    }, [navigate]);

    return (
        <h1>Logging out</h1>
    );
}

export default SplitwiseLogout;
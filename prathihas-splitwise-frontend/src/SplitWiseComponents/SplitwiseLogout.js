import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const SplitwiseLogout = () => 
{
    const navigate = useNavigate();

    useEffect(() => {
        sessionStorage.clear();

        setTimeout(() => navigate('/prathihas-splitwise/login'), 1000);
    }, [navigate]);

    return (
        <h1>Logging out</h1>
    );
}

export default SplitwiseLogout;
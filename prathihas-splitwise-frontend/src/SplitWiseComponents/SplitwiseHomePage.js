import React from "react";
import { NavLink } from 'react-router-dom';

const SplitwiseHomePage = () => {
    const isLoggedIn = sessionStorage.getItem('token');

    return (
        <div>
            <h1>Welcome to My Splitwise</h1>
            <p>A simple site to split and maintain expenses</p>
            {isLoggedIn ? (
                <>
                    <NavLink to="/splitwise/groups">Manage Groups</NavLink>
                    <NavLink to="/splitwise/logout" onClick={() => { sessionStorage.clear(); }}>Logout</NavLink>
                </>
            ) : (
                <div>
                    <NavLink to="/splitwise/login">Login</NavLink>
                    <NavLink to="/splitwise/signup">Sign Up</NavLink>
                </div>
            )}
        </div>
    );
}

export default SplitwiseHomePage;

import logo from './logo.svg';
import './App.css';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import React, {useState, useEffect}  from "react";
import SplitwiseLoginPage from './SplitWiseComponents/SplitwiseLoginPage';
import SplitwiseHomePage from './SplitWiseComponents/SplitwiseHomePage';
import SplitwiseLogout from './SplitWiseComponents/SplitwiseLogout';
import SplitwiseSignupPage from './SplitWiseComponents/SplitwiseSignupPage';

const router = createBrowserRouter([
      {
        path: '/splitwise-home',
        element: <SplitwiseHomePage/>,
      },
      {
        path: '/splitwise-login',
        element: <SplitwiseLoginPage/>
      },
      {
        path: '/splitwise-signup',
        element: <SplitwiseSignupPage/>
      },
      {
        path: '/splitwise-logout',
        element: <SplitwiseLogout/>
      },
      {
        path: '/splitwise-groups',
        element: <SplitwiseGroupsPage/>
      }
])

function App() {

  return (
  <div>
    <RouterProvider router={router} />
  </div>
  );
}

export default App;

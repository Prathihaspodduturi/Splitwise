import './App.css';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';

import React, {useState, useEffect}  from "react";
import SplitwiseLoginPage from './SplitWiseComponents/SplitwiseLoginPage';
import SplitwiseHomePage from './SplitWiseComponents/SplitwiseHomePage';
import SplitwiseLogout from './SplitWiseComponents/SplitwiseLogout';
import SplitwiseSignupPage from './SplitWiseComponents/SplitwiseSignupPage';
import SplitwiseGroupsPage from './SplitWiseComponents/SplitwiseGroupsPage';
import SplitwiseGroupDetail from './SplitWiseComponents/GroupDetail/SplitwiseGroupDetail';
import SplitwiseExpenseDetailPage from './SplitWiseComponents/ExpenseDetail/SplitwiseExpenseDetailPage';
import SplitwiseCreateGroup from './SplitWiseComponents/SplitwiseCreateGroup';


const router = createBrowserRouter([
      {
        path: '/',
        element: <Navigate to="/prathihas-splitwise/login" replace />
      },
      {
        path: '/prathihas-splitwise/',
        element: <SplitwiseHomePage/>,
      },
      {
        path: '/prathihas-splitwise/login',
        element: <SplitwiseLoginPage/>
      },
      {
        path: '/prathihas-splitwise/signup',
        element: <SplitwiseSignupPage/>
      },
      {
        path: '/prathihas-splitwise/logout',
        element: <SplitwiseLogout/>
      },
      {
        path: '/prathihas-splitwise/groups',
        element: <SplitwiseGroupsPage/>
      },
      {
        path: '/prathihas-splitwise/groups/:groupId', // Route for specific group details
        element: <SplitwiseGroupDetail />
      },
])

function App() {

  return (
  <div>
    <RouterProvider router={router} />
  </div>
  );
}

export default App;

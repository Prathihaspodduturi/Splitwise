import './App.css';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

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
        path: '/splitwise/',
        element: <SplitwiseHomePage/>,
      },
      {
        path: '/splitwise/login',
        element: <SplitwiseLoginPage/>
      },
      {
        path: '/splitwise/signup',
        element: <SplitwiseSignupPage/>
      },
      {
        path: '/splitwise/logout',
        element: <SplitwiseLogout/>
      },
      {
        path: '/splitwise/groups',
        element: <SplitwiseGroupsPage/>
      },
      {
        path: '/splitwise/groups/:groupId', // Route for specific group details
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

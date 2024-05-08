# Splitwise Clone

This project is a full-stack application that mimics the functionality of Splitwise, facilitating the management and splitting of expenses among users. It includes a React-based frontend, a Spring Boot backend, and utilizes MySQL for data storage.

## Features

- **User Authentication**: Includes functionalities for users to sign up, log in, and log out.
- **Expense Management**: Users can create groups, add expenses, and split them among group members.
- **Balance Tracking**: Keeps track of how much each user owes or is owed, allowing for easy settlements.

## Tech Stack

- **Frontend**: React, CSS Modules for styling
- **Backend**: Spring Boot, Spring Security for authentication, JWT for session management, Hibernate/Jpa
- **Database**: MySQL
- **Other Tools**: JWT for secure authentication, Fetch API for HTTP requests

## Getting Started

### Prerequisites

- React and npm
- Java Development Kit (JDK)
- Maven
- MySQL Server

### Setting Up the Database

- Start your MySQL server.
- Create the necessary tables by running the SQL scripts provided in the `sql_scripts` directory.

### Setting Up the Backend

- Navigate to the backend directory: `cd path/to/backend`
- Update the `application.properties` file with your MySQL username and password.
- Start the Spring Boot application: `./mvnw spring-boot:run`

### Setting Up the Frontend

- Navigate to the frontend directory: `cd path/to/frontend`
- Install dependencies: `npm install`
- Start the React development server: `npm start`
- Your default web browser should open automatically to `http://localhost:3000`.

## Usage

- **Signing Up**: To use the application, first sign up by navigating to the /signup route and entering a username and password.
- **Logging In**: If you already have an account, log in at the /login route.
- **Managing Expenses**: Once logged in, you can create group to add and manage expenses via the group details page.
- **Settling Balances**: View and settle balances through the user dashboard.
- **Logging Out**: Once your work is done, you can logout.

## Security

This application uses Spring Security and JWT tokens to ensure secure authentication and protect routes.

## Testing

This project includes unit tests for both the frontend and backend, ensuring the reliability and functionality of the application.

### Running Frontend Tests

- Frontend tests are written using Jest and React Testing Library.
- To run these tests, navigate to the frontend directory and use the command: `npm test`
- This command runs all available test suites and outputs the results, including coverage information.
- The project aims for a frontend test coverage of approximately 83%.

### Running Backend Tests

- Backend tests utilize JUnit and Mockito.
- To execute these tests, navigate to the backend directory and run: `./mvnw test`
- This will trigger all tests in the project and display the results, including test coverage statistics.

## Coverage

This project aims for high test coverage to ensure code quality and reliability. Current test coverage stands at approximately 83% for the frontend and similar coverage for the backend, covering major functionalities such as user authentication, expense management, and balance tracking.

# StudBuds Backend

StudBuds is a Spring Boot–based (rigth now just the backend) application for a Study Buddy Matching service. It enables users (with a required @cooper.edu email) to register, log in, update their profiles and preferences, and find study buddies based on shared availability, subjects, and academic courses.

## Features

- **User Authentication**  
  - **Sign Up:** Users can register using a valid @cooper.edu email and a password (minimum 9 characters).  
  - **Login:** Basic login endpoint returning a dummy success message (to be extended).

- **User Management**  
  - Retrieve user details and update user profiles.  
  - Manage user preferences such as available days, subjects to teach, and subjects to learn.

- **Matching Algorithm**  
  - Matches users based on:
    - Shared major and academic year.
    - Overlapping available days.
    - Teaching/learning synergy (one-way or two-way).
  - Provides a match score to rank study buddy suggestions.

- **Testing Endpoints**  
  - Endpoints to list all users and preferences to facilitate testing and debugging.

- **Security**  
  - Configured with Spring Security and BCrypt for password encoding.
  - Permits all endpoints for demo purposes (with scope for future tightening).

- **Containerized Deployment**  
  - Includes a Docker Compose setup with PostgreSQL for streamlined deployment.

## Project Structure

- **Configuration:**  
  - `SecurityConfig.java` – Sets up basic security configurations.

- **Controllers:**  
  - `AuthController.java` – Handles authentication (signup and login).  
  - `UserController.java` – Manages user details and preferences.  
  - `MatchingController.java` – Provides endpoints for matching logic.  
  - `TestController.java` – Contains endpoints for listing all users and preferences.

- **Models & Payloads:**  
  - Domain models: `User.java`, `Preference.java`, `Match.java`.  
  - Request payloads: `SignupRequest.java`, `LoginRequest.java`, and `DeleteAccountRequest.java`.

- **Repositories:**  
  - Data access interfaces for users, preferences, and matches.

- **Services:**  
  - `MatchingService.java` – Implements the matching algorithm and business logic.

- **Configuration Files:**  
  - `pom.xml` – Maven configuration for dependencies and build settings.
  - `application.properties` – Application settings for PostgreSQL and Spring JPA.
  - `docker-compose.yml` – Docker Compose file for containerized deployment with PostgreSQL.

## Prerequisites

- Java 11 installed
- Maven installed
- PostgreSQL (if running locally) or Docker with Docker Compose

## Build and Run Instructions
**Configuration:**

1. Update the database credentials:
   - Open the "application.properties" file located in src/main/resources.
   - Modify the following properties with your PostgreSQL details:
     
2. (If using Docker Compose)
   - Open the "docker-compose.yml" file.
   - Ensure the PostgreSQL environment variables match those in your application.properties:
     
     POSTGRES_DB: studbuds
     POSTGRES_USER: your_username
     POSTGRES_PASSWORD: your_password

**Building and Running with Maven (Locally):**

1. Open a terminal in the project root directory.

2. Build the project:
[code]
mvn clean install
[/code]

3. Run the application:
[code]
mvn spring-boot:run
[/code]

4. Access the application at:
http://localhost:8080


**Running with Docker Compose:**

1. Open a terminal in the project root directory.

2. Build and run the containers:
   Command: docker-compose up --build

3. The backend service will be available at:
   http://localhost:8080
   and PostgreSQL will be running on port 5432.


**Troubleshooting:**
- Database Connection Issues:
  Ensure that the credentials in application.properties and docker-compose.yml are correct and that PostgreSQL is running.
  
- Port Conflicts:
  Verify that ports 8080 (backend) and 5432 (PostgreSQL) are not used by other services.

## API Endpoints:


1. Authentication

a) Sign Up
---------------------
Method: POST
Endpoint: /api/auth/signup
Description: Registers a new user. Email must end with "@cooper.edu" and password must be at least 9 characters.
Request Body:
---------------------
{
    "name": "Your Name",
    "email": "your_email@cooper.edu",
    "password": "your_password",
    "major": "Your Major",
    "year": "Your Year"
}
Expected Response: A success message like "User registered successfully." or an error if the email is already in use.

b) Login
---------------------
Method: POST
Endpoint: /api/auth/login
Description: Authenticates an existing user.
Request Body:
---------------------
{
    "email": "your_email@cooper.edu",
    "password": "your_password"
}
Expected Response: A message indicating "Login successful."

2. User Management

a) Get User Details
---------------------
Method: GET
Endpoint: /api/user/{userId}
Description: Retrieves the details of a user by their ID.
Example URL:
http://localhost:8080/api/user/1
Expected Response: A user object if found, or a 404 error if not.

b) Update or Create User Preference
---------------------
Method: POST
Endpoint: /api/user/{userId}/preference
Description: Sets or updates the preferences for a user.
Request Body:
---------------------
{
    "availableDays": "monday,tuesday",
    "subjectsToLearn": "calculus i,physics",
    "subjectsToTeach": "chemistry"
}
Expected Response: The saved preference details.

3. Matching

a) Find Matches
---------------------
Method: GET
Endpoint: /api/matches/find/{userId}
Description: Retrieves matching suggestions for a given user based on their preferences.
Example URL:
http://localhost:8080/api/matches/find/1
Expected Response: A list of match suggestions including match score, common days, and common subjects. If no preferences are set, a message indicating that will be returned.

b) Connect Match
---------------------
Method: POST
Endpoint: /api/matches/connect
Query Parameters: user1Id (ID of the first user), user2Id (ID of the second user)
Description: Records a match between two users if they have not already been matched.
Example URL:
http://localhost:8080/api/matches/connect?user1Id=1&user2Id=2
Expected Response: The created match object or a 400 error if the users are already matched or if one or both users are not found.

4. Testing Endpoints

a) List All Users
---------------------
Method: GET
Endpoint: /api/test/users
Description: Retrieves all users from the database.
Expected Response: An array of user objects.

b) List All Preferences
---------------------
Method: GET
Endpoint: /api/test/preferences
Description: Retrieves all user preferences.
Expected Response: An array of preference objects.
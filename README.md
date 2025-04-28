# 📚 StudBuds  

**StudBuds** is a full-stack web application that helps students at Cooper Union find the perfect study buddy. Users are matched based on overlapping subjects, availability, and shared interests. The app uses a modern stack with a React frontend, Firebase Authentication, and a robust Spring Boot backend.

---

## 🌟 Features  

- **Secure Authentication:** Firebase-powered signup/login (restricted to `@cooper.edu` emails).
- **User Profiles:** Update name, major, graduation year, and study preferences.
- **Intuitive Preferences UI:** Easily select days and subjects to learn or teach.
- **Swipe & Match System:** Tinder-like interface for finding study partners.
- **Automatic Matching:** Smart matching based on shared availability and subjects.
- **Responsive & Mobile-friendly:** Fully optimized for desktop and mobile browsers.

---

## 💻 Tech Stack  

**Frontend:**  
- React  
- React Router  
- Axios  
- Firebase Authentication  

**Backend:**  
- Java + Spring Boot  
- PostgreSQL  
- Firebase Admin SDK  
- Docker & Docker Compose  

---

## 📂 Project Structure

```
studbuds/
├── backend/
│   ├── src/main/java/com/studbuds/
│   │   ├── config/                 # Security & Firebase configuration
│   │   ├── controller/             # API controllers
│   │   ├── exception/              # Global exception handling
│   │   ├── model/                  # Data models (User, Preferences, etc.)
│   │   ├── payload/                # Request payloads
│   │   └── repository/             # Database repositories
│   ├── src/main/resources/         # App configs & Firebase credentials
│   ├── Dockerfile                  
│   └── pom.xml
│
├── frontend/
│   ├── public/                     # Static assets
│   ├── src/
│   │   ├── components/             # React components
│   │   ├── App.js & App.css        # App root & global styling
│   │   └── firebase-config.js      # Firebase frontend credentials
│   ├── package.json                
│   └── docker-compose.yml          
│
└── README.md
```

---

## 🚀 Quickstart  

### ⚙️ Prerequisites  
- Java 11 or newer  
- Node.js & npm  
- PostgreSQL (or Docker)  
- Firebase project (Auth & Firestore enabled)

### 🔧 Backend Setup  

**Step 1: Firebase Admin SDK**  
- Place your Firebase Admin SDK JSON at:  
```
backend/src/main/resources/firebase-authentication-config.json
```

**Step 2: PostgreSQL Setup**  
- Configure `backend/src/main/resources/application.properties`:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/studbuds
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
```

**Step 3: Run Backend**  
```shell
cd backend
mvn clean install
mvn spring-boot:run
```
Backend API will run on: `http://localhost:8080`

---

### 🌐 Frontend Setup  
![IMG_1494](https://github.com/user-attachments/assets/e46e6f46-8203-4b24-a53e-b71ea7cf222b)
**Step 1: Firebase Config**  
- Configure your Firebase app credentials in:  
```
frontend/src/firebase-config.js
```

**Step 2: Install & Run**  
```shell
cd frontend
npm install
npm start
```

Frontend available at: `http://localhost:3000`

---

### 🐳 Docker (Full Stack Setup)  

- From the root directory:
```shell
docker-compose up --build
```

| Service    | URL                         |
|------------|-----------------------------|
| Frontend   | http://localhost:3000       |
| Backend    | http://localhost:8080       |
| PostgreSQL | localhost:5432              |

---

## 📡 REST API Reference  

### 🔒 Authentication  
- `POST /api/auth/signup`  
- `POST /api/auth/login`  

### 👤 User Preferences  
- `POST /api/user/{userId}/preference`  
- `GET /api/user/{userId}`  

### 🤝 Matching  
- `GET /api/matches/find/{userId}`  
- `POST /api/matches/swipe?user1Id={}&user2Id={}`  
- `GET /api/matches/profile/{userId}`  

### 🛠️ Testing & Debugging  
- `GET /api/test/users`  
- `GET /api/test/preferences`

---

## 🚧 Common Issues & Troubleshooting  

**Q:** "Too many preferences" error when saving?  
**A:** Backend limits the number of subjects. Select fewer subjects.

**Q:** Firebase returns `"auth/too-many-requests"`?  
**A:** Wait and retry later. UI will display `"Bro, slow down! Too many requests."`

**Q:** Unable to login?  
**A:** Verify you're using an `@cooper.edu` email and your password has at least 9 characters.

---

## ✏️ Development Notes  

- Make sure backend and frontend configurations match Firebase and PostgreSQL credentials.
- Frontend error messages are user-friendly—customize in React components as needed.
- Run unit tests (Jest, React Testing Library) and integration tests (Spring Test) frequently.

---
## ⚖️ Ethical Principles 
### 1.02. Moderate the interests of the software engineer, the employer, the client and the users with the public good.
We made sure to balance what we wanted as developers with what would actually help students who would be using the app. We didn’t just think about building features we thought were easy to implement, we focused on what would actually make a difference for students trying to find study partners. At the same time, we stayed mindful of the project expectations and the bigger goal of contributing something positive. We kept the public good in mind by making choices that supported collaboration, learning, and protecting user privacy while keeping in mind our agreed on project deadlines and goals.
### 1.08. Be encouraged to volunteer professional skills to good causes and contribute to public education concerning the discipline:
We volunteered our professional skills to create a software solution that supports students in their academic journeys. By developing Stud Buds, we contributed to the public good by enhancing educational opportunities for users. The app was designed to help students collaborate and learn more effectively, contributing to public education and the ethical use of technology in the learning space.
### 3.10. Ensure adequate testing, debugging, and review of software and related documents on which they work: 
We took adequate steps to ensure the quality of the app by conducting unit tests, which were supplemented by peer code reviews and thorough debugging. Our team also actively engaged in commenting on each other’s code and raising issues and pull requests on GitHub to address any identified problems. This collaborative approach ensured that the software was well-tested and ready for deployment.
### 3.12. Work to develop software and related documents that respect the privacy of those who will be affected by that software:
Stud Buds was designed with privacy in mind. When creating an account, the user is required to check a box relying that by creating an account, they are willing to share their emails with their future matches. Hence, our program is open with what information the system is sharing. If they do not check that box, their account will not be created. We ensured that sensitive information, such as users' academic details and schedules, was stored securely in Firebase, following best practices for data protection. Additionally, when users are swiping on their listed matches, the users' faces are protected by our creation of animal avatars based on user's majors. Our team was mindful of respecting users' privacy by minimizing the amount of personal information required for matching and storing it securely to prevent unauthorized access.
### 6.08. Take responsibility for detecting, correcting, and reporting errors in software and associated documents on which they work.
Throughout the project, we took responsibility for finding and fixing any errors we came across in our code. If someone found a bug or noticed something that did not seem right, we made sure to either open an issue on GitHub or communicate the error in our group chat. We also performed code reviews to catch problems early and made sure to document any changes or fixes we made. This helped us keep Stud Buds reliable and made sure everyone on the team stayed on the same page.
### 8.02. Improve their ability to create safe, reliable, and useful quality software at reasonable cost and within a reasonable time.
Working on Stud Buds helped us get better at building software that is safe, reliable, and useful for real users. We practiced setting realistic goals and sticking to a schedule so we could finish the project on time without rushing important parts like testing and debugging, as outlined in the project proposal. We also learned how to make smart decisions about where to spend our time and resources, making sure we stayed focused on features that would actually help students instead of getting sidetracked. This project taught us a lot about balancing quality with deadlines as well as writing reliable code. Additionally, our app prioritizes safety for users, by keeping their face private through the usage of avatars, being open regarding what information is being shared with other users from the account creation stage, and more. 
### 2.02. Not knowingly use software that is obtained or retained either illegally or unethically:
Throughout the project, we ensured that all the software and libraries we used were licensed appropriately and obtained through legal means. For example, we utilized a package called TinderSwipe for our swiping features, which we uploaded appropriately and legally. Therefore, we adhered to ethical software usage by utilizing open-source tools, ensuring compliance with their licenses, and avoiding any illegal or unethical practices.
### 3.01. Strive for high quality, acceptable cost and a reasonable schedule, ensuring significant tradeoffs are clear to and accepted by the employer and the client, and are available for consideration by the user and the public.
We made sure to deliver a high-quality and appealing app while staying within the time and resource limits we had agreed on in the project proposal as well as project check-ins. When we had to make tradeoffs like cutting out some extra features in order to focus on getting the core parts working smoothly during our initial proposal, we made those decisions as a team and received feedback and approval from our employer/client. While making tradeoffs, we always kept the main goal in mind of creating something that would really help students.  
### 3.05. Ensure an appropriate method is used for any project on which they work or propose to work.
From the start of Stud Buds, we made sure to pick tools and methods that made sense for what we were trying to build, as well as what we learned in our classes. We chose React for the frontend and Firebase for the backend because they were the best fit for creating an easy-to-use app without adding unnecessary complexity. We also followed a scheduled approach, meeting regularly to check in on progress and make quick adjustments when needed. Making these choices early helped us stay organized, avoid bigger problems later on, and build the app in a way that matched our goals.
### 3.11. Ensure adequate documentation, including significant problems discovered and solutions adopted, for any project on which they work.
Throughout the project, we kept detailed documentation of our progress, including code comments, issues found, and solutions implemented. Additionally, we wrote comments for any code that was unclear for any future developers, and continuously updated our README for any important updates to our features, tech stack, etc. For example, we created a UI diagram for our frontend system, highlighting how the requests from the users will be carried through by our features. This documentation ensured that anyone reviewing the project could easily understand the decisions made and how problems were addressed, helping maintain transparency and clarity for future development.

---
## 📖 License  

StudBuds is licensed under the [MIT License](LICENSE).

---

**Made with ❤️ at Cooper Union**

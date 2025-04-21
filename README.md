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
<img width="805" alt="Screenshot 2025-04-21 at 9 27 18 AM" src="https://github.com/user-attachments/assets/3e5e4e1f-fb32-430b-a622-797ace15fca6" />
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

## 📖 License  

StudBuds is licensed under the [MIT License](LICENSE).

---

**Made with ❤️ at Cooper Union**

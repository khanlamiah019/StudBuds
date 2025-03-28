// src/firebase-config.js
import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyDJI4o4Uu5otjFmVoqIdl1iNkisoj61Ge4",
  authDomain: "studbuds-60a97.firebaseapp.com",
  projectId: "studbuds-60a97",
  storageBucket: "studbuds-60a97.firebasestorage.app",
  messagingSenderId: "533248206772",
  appId: "1:533248206772:web:77243753bdacdb285d34e1"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);

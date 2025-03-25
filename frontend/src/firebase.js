import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyDJI4o4Uu5otjFmVoqIdl1iNkisoj61Ge4",
  authDomain: "YOUR_AUTH_DOMAIN",
  projectId: "studbuds-60a97"
};

const firebaseApp = initializeApp(firebaseConfig);
const auth = getAuth(firebaseApp);

export { firebaseApp, auth }; // Add auth export here

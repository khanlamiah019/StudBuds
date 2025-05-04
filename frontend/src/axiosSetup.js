// src/axiosSetup.js
import './firebase-config';
import axios from 'axios';
import { getAuth } from 'firebase/auth';

const BASE_URL = 'https://studbuds-backend.azurewebsites.net'; // ✅ Force Azure for prod

const instance = axios.create({
  baseURL: BASE_URL,
  withCredentials: true, // Just in case cookies are ever used
});

instance.interceptors.request.use(
  async (config) => {
    const auth = getAuth();
    const currentUser = auth.currentUser;
    if (currentUser) {
      const token = await currentUser.getIdToken();
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default instance;
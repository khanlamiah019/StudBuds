// src/axiosSetup.js
import './firebase-config';
import axios from 'axios';
import { getAuth } from 'firebase/auth';

// Automatically choose the backend URL based on the environment
const BASE_URL = process.env.NODE_ENV === 'development'
  ? 'http://localhost:8080' // Local backend for development
  : 'https://studbuds-backend.azurewebsites.net'; // Azure backend for production

const instance = axios.create({
  baseURL: BASE_URL,
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

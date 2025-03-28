// src/axiosSetup.js
import './firebase-config';
import axios from 'axios';
import { getAuth } from 'firebase/auth';

axios.interceptors.request.use(
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

export default axios;
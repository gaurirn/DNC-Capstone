import axios from 'axios';

// This is the public instance for login/signup
const publicApi = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// --- (protectedApi instance is unchanged) ...
const protectedApi = axios.create({
  baseURL: 'http://localhost:8080/api'
});
protectedApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('userToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
protectedApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('userToken');
      localStorage.removeItem('username');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
// --- (End of protectedApi) ---


export const login = async (username, password) => {
  const response = await publicApi.post('/auth/login', { username, password });
  return response.data;
};

// --- THIS FUNCTION IS CHANGED ---
// It now just *sends the OTP*
export const signup = async (userData) => {
  const response = await publicApi.post('/auth/signup', userData);
  return response.data;
};
// --- END OF CHANGE ---

// --- ADD THIS NEW FUNCTION ---
export const verifyOtp = async (code, signupData) => {
  const response = await publicApi.post('/auth/verify', {
    code: code,
    signupRequest: signupData
  });
  return response.data;
};
// --- END OF NEW FUNCTION ---

export const changePassword = async (data) => {
  const response = await protectedApi.post('/auth/change-password', data);
  return response.data;
};

export default publicApi;
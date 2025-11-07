import axios from 'axios';

// Create a new axios instance for Customer routes
const customerApi = axios.create({
  baseURL: 'http://localhost:8080/api/me'
});

// --- REQUEST Interceptor (Adds the token) ---
customerApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('userToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// --- RESPONSE Interceptor (Handles 401 errors) ---
customerApi.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('userToken');
      localStorage.removeItem('username');
      window.location.href = '/login'; 
    }
    return Promise.reject(error);
  }
);

// --- Dashboard & Payment ---
export const getMyStatus = () => customerApi.get('/status');
export const addBalance = (amount) => customerApi.post('/add-balance', { amount });
export const payMyBill = () => customerApi.post('/payment');
export const getPaymentHistory = () => customerApi.get('/payment-history');

// --- Plan & Subscription ---
export const getAvailablePlans = () => customerApi.get('/plans');
export const getMySubscriptions = () => customerApi.get('/subscriptions');
export const subscribeToPlan = (planId) => customerApi.post(`/subscribe/${planId}`);
export const cancelSubscription = (subscriptionId) => customerApi.delete(`/subscriptions/${subscriptionId}`);

// --- Profile ---
export const updateMyProfile = (profileData) => customerApi.put('/profile', profileData);

// --- Notifications ---
export const getMyNotifications = () => customerApi.get('/notifications');
export const sendChatMessage = (message) => customerApi.post('/chat', { message });
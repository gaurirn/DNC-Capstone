// src/api/adminService.js
import axios from 'axios';

// Create a new axios instance for Admin routes
const adminApi = axios.create({
  baseURL: 'http://localhost:8080/api/admin', // matches your Spring mapping prefix
  // optional: timeout: 10000,
});

// --- REQUEST Interceptor (Adds the token) ---
adminApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('userToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    // Ensure JSON content-type for PUT/POST with body
    if (!config.headers['Content-Type'] && ['post', 'put', 'patch'].includes((config.method || '').toLowerCase())) {
      config.headers['Content-Type'] = 'application/json';
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// --- RESPONSE Interceptor (Handles 401 errors) ---
adminApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('userToken');
      localStorage.removeItem('username');
      // You might want a nicer redirect flow depending on your app router
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// --- Define all our Admin API calls ---
// Dashboard
export const getDashboardStats = () => adminApi.get('/stats');
export const getTopRiskAccounts = () => adminApi.get('/top-risk-accounts');

// Dashboard triggers
export const triggerInvoiceCreation = () => adminApi.post('/trigger/invoice-creation');
export const triggerBillingCycle = () => adminApi.post('/trigger/billing-cycle');
export const triggerDunning = () => adminApi.post('/trigger/dunning');
export const triggerUsage = () => adminApi.post('/trigger/usage');

// Customers
export const getAllCustomers = () => adminApi.get('/customers');
export const updateCustomer = (id, customerData) => adminApi.put(`/customers/${id}`, customerData);

// Rules
export const getAllRules = () => adminApi.get('/rules');
export const createRule = (ruleData) => adminApi.post('/rules', ruleData);
export const updateRule = (id, ruleData) => adminApi.put(`/rules/${id}`, ruleData);
export const deleteRule = (id) => adminApi.delete(`/rules/${id}`);

// Logs & curing & payments
export const getAllLogs = () => adminApi.get('/logs');
export const getCuredPayments = () => adminApi.get('/cured-payments');
export const getAllPayments = () => adminApi.get('/payments');

// Invoices for a customer
export const getCustomerInvoices = (customerId) => {
  // NOTE: baseURL already contains /api/admin, so we only use the suffix here
  return adminApi.get(`/customers/${customerId}/invoices`);
};

// Update invoice due date
export const updateInvoiceDueDate = (invoiceId, dueDate) => {
  // Backend expects JSON: { dueDate: "YYYY-MM-DD" }
  return adminApi.put(`/invoices/${invoiceId}/due-date`, { dueDate });
};

export const sendAdminChatMessage = (message) => {
  return adminApi.post('/chat', { message });
};

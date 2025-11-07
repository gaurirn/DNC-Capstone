import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';

const useAuth = () => {
  const token = localStorage.getItem('userToken');
  return { isAuthenticated: !!token };
};

const ProtectedRoute = () => {
  const { isAuthenticated } = useAuth();
  
  if (!isAuthenticated) {
    // Not logged in, redirect to login page
    return <Navigate to="/login" replace />;
  }
  
  // Logged in, show the child route (e.g., <AdminLayout />)
  return <Outlet />; 
};

export default ProtectedRoute;
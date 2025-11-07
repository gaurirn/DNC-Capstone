import React from 'react';
import { Outlet } from 'react-router-dom';
import CustomerNavbar from '../components/CustomerNavbar';
import { Container } from 'react-bootstrap';
import CustomerChatbot from '../components/CustomerChatbot'; // <-- IMPORT

function CustomerLayout() {
  return (
    <div className="customer-layout d-flex flex-column min-vh-100">
      <CustomerNavbar />
      <Container fluid="lg" className="py-4 flex-grow-1">
        <Outlet />
      </Container>
      
      {/* --- ADD THIS --- */}
      <CustomerChatbot />
      {/* --- END OF ADDITION --- */}

      <footer className="bg-light text-center p-3 mt-auto">
        &copy; 2025 Dunning & Curing Management System
      </footer>
    </div>
  );
}

export default CustomerLayout;
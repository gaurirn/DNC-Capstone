import React from 'react';
import { Outlet, Link, useNavigate, NavLink } from 'react-router-dom';
import { Container, Nav, Navbar, Button, NavDropdown } from 'react-bootstrap';
import { BoxArrowRight, PersonCircle } from 'react-bootstrap-icons';
import CustomerChatbot from '../components/CustomerChatbot'; // Import the chatbot

function CustomerLayout() {
  const navigate = useNavigate();
  const username = localStorage.getItem('username') || 'Customer';

  const handleLogout = () => {
    localStorage.removeItem('userToken');
    localStorage.removeItem('username');
    navigate('/login');
  };

  return (
    <div className="customer-layout">
      {/* Top Header */}
      <Navbar bg="dark" variant="dark" expand="lg" className="shadow-sm">
        <Container fluid>
          <Navbar.Brand as={Link} to="/dashboard" style={{ color: '#6d8ad1', fontWeight: '700' }}>
            <span className="auth-logo-icon">$</span> RevenueGuard
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="customer-navbar-nav" />
          <Navbar.Collapse id="customer-navbar-nav">
            <Nav className="me-auto">
              <Nav.Link as={NavLink} to="/dashboard" end>Dashboard</Nav.Link>
              <Nav.Link as={NavLink} to="/dashboard/plans">Browse Plans</Nav.Link>
              <Nav.Link as={NavLink} to="/dashboard/subscriptions">My Subscriptions</Nav.Link>
              <Nav.Link as={NavLink} to="/dashboard/payment-history">Payment History</Nav.Link>
              <Nav.Link as={NavLink} to="/dashboard/notifications">Notifications</Nav.Link>
            </Nav>
            <Nav className="ms-auto d-flex flex-row align-items-center">
              <NavDropdown title={<PersonCircle size={24} />} id="profile-dropdown" align="end">
                <NavDropdown.ItemText className="text-muted">
                  Signed in as<br /><strong>{username}</strong>
                </NavDropdown.ItemText>
                <NavDropdown.Divider />
                <NavDropdown.Item as={Link} to="/dashboard/profile">
                  Profile & Account
                </NavDropdown.Item>
                <NavDropdown.Item onClick={handleLogout} className="text-danger">
                  <BoxArrowRight className="me-2" /> Logout
                </NavDropdown.Item>
              </NavDropdown>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      {/* Main Content Area */}
      <Container className="mt-4">
        <Outlet /> 
      </Container>

      {/* Add Customer Chatbot - appears on all customer pages */}
      <CustomerChatbot />
    </div>
  );
}

export default CustomerLayout;

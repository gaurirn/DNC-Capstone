import React from 'react';
import { Outlet, Link, useNavigate, NavLink } from 'react-router-dom';
import { Container, Row, Col, Nav, Navbar, Button } from 'react-bootstrap';
// Import the icons
import { 
  BoxArrowRight, LayoutSidebarInset, People, Sliders, PlayBtn, 
  ShieldCheck, CreditCard, BarChart, Gear 
} from 'react-bootstrap-icons';
import './AdminLayout.css'; // Your existing CSS file

// --- IMPORT THE CHATBOT ---
import AdminChatbot from '../components/admin/AdminChatbot';

// --- (Your existing AdminAvatar helper component is correct) ---
function AdminAvatar() {
  const username = localStorage.getItem('username') || 'A'; // Default to 'A'
  const initial = username.charAt(0).toUpperCase();

  return (
    <div className="admin-avatar ms-3">
      {initial}
    </div>
  );
}

function AdminLayout() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('userToken');
    localStorage.removeItem('username'); 
    navigate('/login');
  };

  return (
    <div className="admin-layout">
      {/* Top Header (Your code is correct) */}
      <Navbar bg="light" expand="lg" className="admin-header shadow-sm">
        <Container fluid>
          <Navbar.Brand as={Link} to="/admin/dashboard" style={{ color: '#d16d8a', fontWeight: '700' }}>
            <span className="auth-logo-icon">$</span> RevenueGuard
          </Navbar.Brand>
          <Nav className="ms-auto d-flex flex-row align-items-center">
            <Button variant="outline-danger" onClick={handleLogout} size="sm">
              <BoxArrowRight className="me-1" /> Logout
            </Button>
            <AdminAvatar />
          </Nav>
        </Container>
      </Navbar>

      <Container fluid>
        <Row>
          {/* Sidebar (Your code is correct) */}
          <Col md={2} className="admin-sidebar bg-light">
            <Nav defaultActiveKey="/admin/dashboard" className="flex-column">
              <Nav.Link as={NavLink} to="/admin/dashboard"><LayoutSidebarInset className="me-2" /> Dashboard</Nav.Link>
              <Nav.Link as={NavLink} to="/admin/customers"><People className="me-2" /> Customers</Nav.Link>
              <Nav.Link as={NavLink} to="/admin/rules"><Sliders className="me-2" /> Rules Engine</Nav.Link>
              <Nav.Link as={NavLink} to="/admin/dunning"><PlayBtn className="me-2" /> Dunning</Nav.Link>
              <Nav.Link as={NavLink} to="/admin/curing"><ShieldCheck className="me-2" /> Curing</Nav.Link>
              <Nav.Link as={NavLink} to="/admin/payments"><CreditCard className="me-2" /> Payments</Nav.Link>
              <Nav.Link as={NavLink} to="/admin/settings"><Gear className="me-2" /> Settings</Nav.Link>
            </Nav>
          </Col>
          
          {/* Main Content Area (Your code is correct) */}
          <Col md={10} className="admin-content">
            <Outlet /> 
          </Col>
        </Row>
      </Container>
      
      {/* --- THIS IS THE FIX --- */}
      {/* Add the chatbot component here, outside the main layout grid */}
      <AdminChatbot />
      {/* --- END OF FIX --- */}
    </div>
  );
}
export default AdminLayout;

import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login } from '../api/authService';
import { Form, Button, Container, Card, Alert, Spinner } from 'react-bootstrap';

function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = await login(username, password);
      // --- THIS IS THE CHANGE ---
      localStorage.setItem('userToken', data.token);
      localStorage.setItem('username', data.username); // <-- ADD THIS LINE
      // --- END OF CHANGE ---
      // ... (redirection logic) ...
      if (data.roles.includes('ROLE_ADMIN')) navigate('/admin/dashboard');
      else if (data.roles.includes('ROLE_SUPPORT_AGENT')) navigate('/support/dashboard');
      else navigate('/dashboard');

    } catch (err) {
      setError('Invalid username or password.');
      setLoading(false);
    }
  };

  return (
    <Container fluid className="d-flex align-items-center justify-content-center vh-100">
      
      <Card className="auth-card">
        <Card.Body>
          {/* Logo and Title */}
          <div className="auth-logo">
            <span className="auth-logo-icon">$</span> RevenueGuard
          </div>
          <h3 className="text-center mb-4" style={{ color: '#333' }}>Login</h3>
          
          <Form onSubmit={handleSubmit}>
            {error && <Alert variant="danger">{error}</Alert>}
            
            <Form.Group className="mb-3" controlId="formEmail">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                placeholder="Enter your email address"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </Form.Group>

            <Form.Group className="mb-4" controlId="formPassword">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <div className="text-end mt-2">
                <Link to="/forgot-password" style={{ fontSize: '0.9rem', color: '#777' }}>
                  Forgot Password?
                </Link>
              </div>
            </Form.Group>

            <div className="d-grid">
              <Button className="btn-custom" type="submit" disabled={loading}>
                {loading ? (
                  <Spinner as="span" animation="border" size="sm" />
                ) : (
                  'Sign In'
                )}
              </Button>
            </div>

            <p className="text-center text-muted mt-4">
              Don't have an account? 
              <Link to="/signup" className="ms-1 fw-bold" style={{ color: '#d16d8a' }}>Sign Up</Link>
            </p>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
}

export default LoginPage;
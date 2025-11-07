import React, { useState } from 'react';
import { Form, Button, Card, Col, Row, Spinner, Alert } from 'react-bootstrap';
import { PersonCircle, Lock, LockFill } from 'react-bootstrap-icons';
import { useNavigate } from 'react-router-dom';
import { changePassword } from '../../api/authService';

function SettingsPage() {
  const [formData, setFormData] = useState({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  // Get username from localStorage to display
  const username = localStorage.getItem('username') || 'Admin';
  const initial = username.charAt(0).toUpperCase();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // 1. Frontend validation
    if (formData.newPassword !== formData.confirmPassword) {
      setError('New passwords do not match.');
      return;
    }
    if (formData.newPassword.length < 6) {
      setError('New password must be at least 6 characters.');
      return;
    }

    setLoading(true);
    try {
      // 2. Call API
      const response = await changePassword(formData.oldPassword, formData.newPassword);
      setSuccess(response.message);
      setFormData({ oldPassword: '', newPassword: '', confirmPassword: '' }); // Clear form
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to change password.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('userToken');
    localStorage.removeItem('username');
    navigate('/login');
  };

  return (
    <>
      <h2 className="mb-4">Settings</h2>
      <Row>
        <Col md={4}>
          <Card className="shadow-sm text-center p-4">
            <div className="admin-avatar mx-auto mb-3" style={{ width: '90px', height: '90px', fontSize: '3rem' }}>
              {initial}
            </div>
            <Card.Body>
              <Card.Title>Welcome, {username}</Card.Title>
              <Card.Text className="text-muted">
                You are logged in as an Administrator.
              </Card.Text>
              <Button variant="danger" onClick={handleLogout} className="mt-3">
                Logout
              </Button>
            </Card.Body>
          </Card>
        </Col>
        <Col md={8}>
          <Card className="shadow-sm">
            <Card.Header as="h5">Change Password</Card.Header>
            <Card.Body>
              {error && <Alert variant="danger">{error}</Alert>}
              {success && <Alert variant="success">{success}</Alert>}
              <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="oldPassword">
                  <Form.Label>Old Password</Form.Label>
                  <Form.Control
                    type="password"
                    name="oldPassword"
                    value={formData.oldPassword}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
                
                <Form.Group className="mb-3" controlId="newPassword">
                  <Form.Label>New Password</Form.Label>
                  <Form.Control
                    type="password"
                    name="newPassword"
                    value={formData.newPassword}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
                
                <Form.Group className="mb-3" controlId="confirmPassword">
                  <Form.Label>Confirm New Password</Form.Label>
                  <Form.Control
                    type="password"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
                
                <div className="d-flex justify-content-end">
                  <Button variant="primary" type="submit" disabled={loading}>
                    {loading ? <Spinner as="span" animation="border" size="sm" /> : 'Update Password'}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </>
  );
}

export default SettingsPage;
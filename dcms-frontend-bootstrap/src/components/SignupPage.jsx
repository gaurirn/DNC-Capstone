import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Card, Form, Button, Alert, Container, Row, Col, Spinner } from 'react-bootstrap';
import { signup, verifyOtp } from '../api/authService';

function SignupPage() {
  const [step, setStep] = useState(1); // 1 for details, 2 for OTP
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phone: '',
    segment: 'POSTPAID' // Default to POSTPAID
  });
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  // Step 1: Handle sending the signup details to get an OTP
  const handleSignupSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    // --- THIS IS THE FIX ---
    // Simple frontend validation
    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    // Add phone number validation
    if (formData.phone.length < 10) {
      setError('Phone number must be at least 10 digits.');
      return;
    }
    // --- END OF FIX ---

    // Auto-set username to be the same as email
    const dataToSubmit = {
      ...formData,
      username: formData.email
    };

    setLoading(true);
    try {
      // This now just sends the OTP
      const response = await signup(dataToSubmit);
      setFormData(dataToSubmit); // Save the data (including username)
      setSuccess(response.message); // "Verification code sent..."
      setStep(2); // Move to OTP step
    } catch (err) {
      setError(err.response?.data?.message || 'Signup failed.');
    } finally {
      setLoading(false);
    }
  };

  // Step 2: Handle sending the OTP and user data to verify
  const handleVerifySubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      // Send the OTP *and* the original form data
      const response = await verifyOtp(otp, formData);
      setSuccess(response.message); // "User registered successfully!"
      
      // Redirect to login after a short delay
      setTimeout(() => {
        navigate('/login');
      }, 2000);

    } catch (err) {
      setError(err.response?.data?.message || 'OTP verification failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <Container>
        <Row className="justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
          <Col xs={12} sm={10} md={8} lg={6} xl={5}>
            <Card className="shadow-lg auth-card">
              <Card.Body className="p-4 p-md-5">
                <div className="text-center mb-4">
                  <h1 className="auth-logo"><span className="auth-logo-icon">$</span> RevenueGuard</h1>
                  <h3 className="text-muted">
                    {step === 1 ? 'Create Your Account' : 'Verify Your Email'}
                  </h3>
                </div>

                {error && <Alert variant="danger">{error}</Alert>}
                {success && <Alert variant="success">{success}</Alert>}

                {/* --- STEP 1: SIGNUP FORM --- */}
                {step === 1 && (
                  <Form onSubmit={handleSignupSubmit}>
                    <Row>
                      <Col xs={12} sm={6}>
                        <Form.Group className="mb-3">
                          <Form.Label>First Name</Form.Label>
                          <Form.Control type="text" name="firstName" onChange={handleChange} required />
                        </Form.Group>
                      </Col>
                      <Col xs={12} sm={6}>
                        <Form.Group className="mb-3">
                          <Form.Label>Last Name</Form.Label>
                          <Form.Control type="text" name="lastName" onChange={handleChange} required />
                        </Form.Group>
                      </Col>
                    </Row>
                    
                    <Form.Group className="mb-3">
                      <Form.Label>Email (This will be your Username)</Form.Label>
                      <Form.Control type="email" name="email" onChange={handleChange} required />
                    </Form.Group>
                    
                    <Form.Group className="mb-3">
                      <Form.Label>Phone</Form.Label>
                      <Form.Control type="tel" name="phone" onChange={handleChange} required />
                    </Form.Group>

                    <Form.Group className="mb-3">
                      <Form.Label>Password</Form.Label>
                      <Form.Control type="password" name="password" onChange={handleChange} required />
                    </Form.Group>

                    <Form.Group className="mb-3">
                      <Form.Label>Account Type</Form.Label>
                      <Form.Select name="segment" value={formData.segment} onChange={handleChange}>
                        <option value="POSTPAID">Postpaid (Monthly Bill)</option>
                        <option value="PREPAID">Prepaid (Pay as you go)</option>
                      </Form.Select>
                    </Form.Group>

                    <Button variant="primary" type="submit" className="w-100" disabled={loading}>
                      {loading ? <Spinner animation="border" size="sm" /> : 'Get Verification Code'}
                    </Button>
                  </Form>
                )}

                {/* --- STEP 2: OTP FORM --- */}
                {step === 2 && (
                  <Form onSubmit={handleVerifySubmit}>
                    <p className="text-center">A 6-digit code was sent to <strong>{formData.email}</strong>. Please enter it below.</p>
                    <Form.Group className="mb-3">
                      <Form.Label>Verification Code</Form.Label>
                      <Form.Control
                        type="text"
                        name="otp"
                        maxLength="6"
                        onChange={(e) => setOtp(e.target.value)}
                        required
                        style={{ fontSize: '1.5rem', textAlign: 'center', letterSpacing: '0.5rem' }}
                      />
                    </Form.Group>
                    
                    <Button variant="success" type="submit" className="w-100" disabled={loading}>
                      {loading ? <Spinner animation="border" size="sm" /> : 'Verify and Create Account'}
                    </Button>

                    <Button variant="link" onClick={() => { setStep(1); setError(''); setSuccess(''); }} className="w-100 mt-2">
                      Need to change your details? Go back.
                    </Button>
                  </Form>
                )}

                <div className="text-center mt-4">
                  Already have an account? <Link to="/login">Log In</Link>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default SignupPage;
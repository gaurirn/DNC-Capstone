import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Spinner, Alert, Badge, ListGroup, Form } from 'react-bootstrap';
import { getMyStatus, addBalance, payMyBill } from '../api/customerService';

// Main Dashboard Component
function CustomerDashboard() {
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [fundAmount, setFundAmount] = useState(50);

  useEffect(() => {
    fetchStatus();
  }, []);

  const fetchStatus = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await getMyStatus();
      setStatus(response.data);
    } catch (err) {
      setError('Failed to fetch account status.');
    } finally {
      setLoading(false);
    }
  };

  const handleAddFunds = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      await addBalance(fundAmount);
      setSuccess(`Successfully added $${fundAmount} to your balance.`);
      fetchStatus();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add funds.');
      setLoading(false);
    }
  };

  const handlePayBill = async () => {
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      await payMyBill();
      setSuccess('Payment successful! Your outstanding bill has been paid.');
      fetchStatus();
    } catch (err) {
      setError(err.response?.data?.message || 'Payment failed.');
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center"><Spinner animation="border" /></div>;
  }

  if (error && !status) {
    return <Alert variant="danger">{error}</Alert>;
  }

  if (!status) {
    return <Alert variant="warning">Could not load customer data.</Alert>;
  }

  // Determine status banner style
  const isBlocked = status.profile.status === 'BLOCKED';
  const isThrottled = status.profile.status === 'THROTTLED';
  const showWarning = isBlocked || isThrottled;

  return (
    <>
      {/* Service Status Banner */}
      {showWarning && (
        <Alert 
          variant={isBlocked ? 'danger' : 'warning'} 
          className="text-center py-4 mb-4"
          style={{ fontSize: '1.5rem', fontWeight: '600' }}
        >
          {isBlocked ? '🚫 SERVICE BARRED - Immediate Action Required' : '⚠️ SERVICE THROTTLED - Payment Overdue'}
        </Alert>
      )}

      {/* Restore Service Button */}
      {showWarning && (
        <Button 
          variant="primary" 
          size="lg"
          className="w-100 mb-4 py-3"
          style={{ fontSize: '1.2rem', fontWeight: '600' }}
          onClick={handlePayBill}
          disabled={loading || status.profile.amountOverdue === 0}
        >
          RESTORE SERVICE NOW
        </Button>
      )}

      {/* Payment Suggestion Box */}
      {showWarning && status.profile.amountOverdue > 0 && (
        <Alert variant="light" className="text-center mb-4" style={{ backgroundColor: '#f0f4f8', border: '1px solid #d1dce5' }}>
          Pay ${(status.profile.amountOverdue / 2).toFixed(2)} now to keep service active and split the rest.
        </Alert>
      )}

      {/* Success/Error Messages */}
      {error && <Alert variant="danger">{error}</Alert>} 
      {success && <Alert variant="success">{success}</Alert>}

      {/* Account Overview Section */}
      <h3 className="mb-4 mt-5">Account Overview</h3>
      
      <Row className="g-4 mb-5">
        {/* Current Plan Card */}
        <Col md={4}>
          <Card className="h-100 shadow-sm" style={{ backgroundColor: '#ffe4b5', border: 'none' }}>
            <Card.Body className="d-flex flex-column justify-content-center">
              <small className="text-muted mb-2">Current Plan</small>
              <h4 className="mb-0">
                {status.activeSubscriptions.length > 0 
                  ? status.activeSubscriptions[0].plan.planName 
                  : 'No Active Plan'}
              </h4>
            </Card.Body>
          </Card>
        </Col>

        {/* Next Billing Date Card */}
        <Col md={4}>
          <Card className="h-100 shadow-sm" style={{ backgroundColor: '#c8e6c9', border: 'none' }}>
            <Card.Body className="d-flex flex-column justify-content-center">
              <small className="text-muted mb-2">Next Billing Date</small>
              <h4 className="mb-0">
                {status.profile.dueDate 
                  ? new Date(status.profile.dueDate).toLocaleDateString('en-US')
                  : 'N/A'}
              </h4>
            </Card.Body>
          </Card>
        </Col>

        {/* Full Overdue Amount Card */}
        <Col md={4}>
          <Card className="h-100 shadow-sm" style={{ backgroundColor: '#e1bee7', border: 'none' }}>
            <Card.Body className="d-flex flex-column justify-content-center">
              <small className="text-muted mb-2">Full Overdue Amount</small>
              <h4 className="mb-0">${status.profile.amountOverdue.toFixed(2)}</h4>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* AI Assistant Button */}
      <Card className="shadow-sm mb-5" style={{ backgroundColor: '#f8f9fa', border: '1px solid #e0e0e0' }}>
        <Card.Body className="text-center py-4">
          <Button variant="outline-dark" size="lg" style={{ minWidth: '200px' }}>
            🤖 AI Assistant
          </Button>
        </Card.Body>
      </Card>

      {/* Detailed Information Section */}
      <Row className="g-4">
        {/* Left Column - Subscriptions & Status */}
        <Col md={7}>
          {/* Account Status Card */}
          <Card className="shadow-sm mb-4">
            <Card.Header as="h5">Account Status</Card.Header>
            <Card.Body>
              <div className="d-flex align-items-center">
                <Badge 
                  bg={status.profile.status === 'ACTIVE' ? 'success' : status.profile.status === 'THROTTLED' ? 'warning' : 'danger'}
                  className="me-3"
                  style={{ fontSize: '1rem', padding: '0.5rem 1rem' }}
                >
                  {status.profile.status}
                </Badge>
                <p className="mb-0">
                  {status.profile.status === 'ACTIVE' && 'Your account is active and all services are operational.'}
                  {status.profile.status === 'THROTTLED' && 'Your account is overdue. Service speed has been reduced.'}
                  {status.profile.status === 'BLOCKED' && 'Your account is severely overdue. All services have been blocked.'}
                  {status.profile.status === 'INACTIVE' && 'Your account is currently inactive.'}
                </p>
              </div>
            </Card.Body>
          </Card>

          {/* Subscriptions Card */}
          <Card className="shadow-sm">
            <Card.Header as="h5">My Subscriptions</Card.Header>
            <Card.Body>
              <ListGroup variant="flush">
                {status.activeSubscriptions.length > 0 ? (
                  status.activeSubscriptions.map(sub => (
                    <ListGroup.Item key={sub.id} className="d-flex justify-content-between align-items-center">
                      <div>
                        <strong>{sub.plan.planName}</strong> ({sub.plan.serviceType})
                        <br />
                        <small className="text-muted">Subscribed on: {new Date(sub.startDate).toLocaleDateString()}</small>
                      </div>
                      <Badge bg="primary">${sub.plan.price.toFixed(2)}/mo</Badge>
                    </ListGroup.Item>
                  ))
                ) : (
                  <p className="text-muted p-3">You have no active subscriptions.</p>
                )}
              </ListGroup>
            </Card.Body>
          </Card>
        </Col>

        {/* Right Column - Billing & Payments */}
        <Col md={5}>
          {/* Billing Summary Card */}
          <Card className="shadow-sm mb-4">
            <Card.Header as="h5">Billing Summary</Card.Header>
            <Card.Body>
              <div className="text-center mb-3">
                <h6 className="text-muted">CURRENT BALANCE</h6>
                <h1 className="display-5 fw-bold text-success">
                  ${status.profile.balance.toFixed(2)}
                </h1>
              </div>
              <div className="text-center mb-3">
                <h6 className="text-muted">AMOUNT DUE</h6>
                <h1 className="display-5 fw-bold text-danger">
                  ${status.profile.amountOverdue.toFixed(2)}
                </h1>
                {status.profile.dueDate && (
                  <small>Due on {new Date(status.profile.dueDate).toLocaleDateString()}</small>
                )}
              </div>
              
              <Button 
                variant="danger" 
                className="w-100" 
                onClick={handlePayBill}
                disabled={loading || status.profile.amountOverdue === 0}
              >
                {loading ? <Spinner as="span" animation="border" size="sm" /> : 'Pay Outstanding Bill'}
              </Button>
            </Card.Body>
          </Card>

          {/* Add Funds Card */}
          <Card className="shadow-sm">
            <Card.Header as="h5">Add Funds</Card.Header>
            <Card.Body>
              <Form onSubmit={handleAddFunds}>
                <Form.Group className="mb-3">
                  <Form.Label>Amount ($)</Form.Label>
                  <Form.Control 
                    type="number"
                    min="5"
                    step="0.01"
                    value={fundAmount}
                    onChange={(e) => setFundAmount(e.target.value)}
                  />
                </Form.Group>
                <Button variant="success" type="submit" className="w-100" disabled={loading}>
                  {loading ? <Spinner as="span" animation="border" size="sm" /> : 'Add Funds to Wallet'}
                </Button>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </>
  );
}

export default CustomerDashboard;
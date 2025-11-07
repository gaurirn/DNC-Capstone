import React, { useState, useEffect } from 'react';
// --- THIS IS THE FIX ---
import { Card, Row, Col, Button, Spinner, Alert, Badge, ListGroup } from 'react-bootstrap';
// --- END OF FIX ---
import { getAvailablePlans, subscribeToPlan } from '../api/customerService';
import { useNavigate } from 'react-router-dom';

function PlanCard({ plan, onSubscribe, loading }) {
  return (
    <Card className="h-100 shadow-sm">
      <Card.Body className="d-flex flex-column">
        <Card.Title className="fw-bold">{plan.planName}</Card.Title>
        <Card.Subtitle className="mb-2 text-muted">{plan.description}</Card.Subtitle>
        <div className="my-3">
          <span className="display-6 fw-bold">${plan.price.toFixed(2)}</span>
          <span className="text-muted">/month</span>
        </div>
        <ListGroup variant="flush" className="flex-grow-1">
          <ListGroup.Item>
            <strong>Type:</strong> <Badge bg="info">{plan.type}</Badge>
          </ListGroup.Item>
          <ListGroup.Item>
            <strong>Data:</strong> {plan.dataLimitMb === 0 ? 'Unlimited' : `${plan.dataLimitMb / 1024} GB`}
          </ListGroup.Item>
        </ListGroup>
        <Button 
          variant="primary" 
          className="w-100 mt-3" 
          onClick={() => onSubscribe(plan.id)}
          disabled={loading}
        >
          {loading ? <Spinner as="span" animation="border" size="sm" /> : 'Subscribe Now'}
        </Button>
      </Card.Body>
    </Card>
  );
}

function BrowsePlans() {
  const [plans, setPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [subscribingId, setSubscribingId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchPlans();
  }, []);

  const fetchPlans = async () => {
    try {
      setLoading(true);
      const response = await getAvailablePlans();
      setPlans(response.data);
    } catch (err) {
      setError('Failed to fetch plans.');
    } finally {
      setLoading(false);
    }
  };

  const handleSubscribe = async (planId) => {
    setSubscribingId(planId);
    setError('');
    setSuccess('');
    try {
      const response = await subscribeToPlan(planId);
      setSuccess(response.data.message + " Redirecting to dashboard...");
      
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Subscription failed.');
      setSubscribingId(null);
    }
  };

  if (loading) {
    return <div className="text-center"><Spinner animation="border" /></div>;
  }

  return (
    <>
      <h2 className="mb-4">Browse Available Plans</h2>
      {error && <Alert variant="danger">{error}</Alert>}
      {success && <Alert variant="success">{success}</Alert>}
      
      {plans.length === 0 && !loading && (
        <Alert variant="info">No plans are currently available for your account type.</Alert>
      )}

      <Row xs={1} md={2} lg={3} className="g-4">
        {plans.map(plan => (
          <Col key={plan.id}>
            <PlanCard 
              plan={plan} 
              onSubscribe={handleSubscribe} 
              loading={subscribingId === plan.id}
            />
          </Col>
        ))}
      </Row>
    </>
  );
}

export default BrowsePlans;
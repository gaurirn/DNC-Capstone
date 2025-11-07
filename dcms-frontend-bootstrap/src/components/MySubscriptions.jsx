import React, { useState, useEffect } from 'react';
import { Card, Button, Spinner, Alert, Badge, ListGroup, Modal } from 'react-bootstrap';
import { getMySubscriptions, cancelSubscription } from '../api/customerService';
import { Link } from 'react-router-dom';

function MySubscriptions() {
  const [subscriptions, setSubscriptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [cancelingId, setCancelingId] = useState(null);

  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [selectedSub, setSelectedSub] = useState(null);

  useEffect(() => {
    fetchSubscriptions();
  }, []);

  const fetchSubscriptions = async () => {
    try {
      setLoading(true);
      const response = await getMySubscriptions();
      setSubscriptions(response.data);
    } catch (err) {
      setError('Failed to fetch subscriptions.');
    } finally {
      setLoading(false);
    }
  };

  const openCancelModal = (sub) => {
    setSelectedSub(sub);
    setShowModal(true);
  };

  const closeCancelModal = () => {
    setShowModal(false);
    setSelectedSub(null);
  };

  const handleCancel = async () => {
    if (!selectedSub) return;

    setCancelingId(selectedSub.id);
    setError('');
    setSuccess('');
    try {
      await cancelSubscription(selectedSub.id);
      setSuccess('Subscription canceled successfully.');
      fetchSubscriptions(); // Refresh the list
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to cancel subscription.');
    } finally {
      setCancelingId(null);
      closeCancelModal();
    }
  };

  if (loading) {
    return <div className="text-center"><Spinner animation="border" /></div>;
  }

  return (
    <>
      <h2 className="mb-4">My Subscriptions</h2>
      {error && <Alert variant="danger">{error}</Alert>}
      {success && <Alert variant="success">{success}</Alert>}

      <Card className="shadow-sm">
        <Card.Header as="h5">Active Subscriptions</Card.Header>
        <Card.Body>
          <ListGroup variant="flush">
            {subscriptions.filter(s => s.status === 'ACTIVE').length > 0 ? (
              subscriptions.filter(s => s.status === 'ACTIVE').map(sub => (
                <ListGroup.Item key={sub.id} className="d-flex justify-content-between align-items-center">
                  <div>
                    <strong>{sub.plan.planName}</strong> (${sub.plan.price.toFixed(2)}/mo)
                    <br />
                    <small className="text-muted">Subscribed on: {new Date(sub.activationDate).toLocaleDateString()}</small>
                  </div>
                  <Button variant="outline-danger" size="sm" onClick={() => openCancelModal(sub)}>
                    Cancel
                  </Button>
                </ListGroup.Item>
              ))
            ) : (
              <p className="text-muted p-3">
                You have no active subscriptions. <Link to="/dashboard/plans">Browse plans</Link> to get started.
              </p>
            )}
          </ListGroup>
        </Card.Body>
      </Card>

      <Card className="shadow-sm mt-4">
        <Card.Header as="h5">Canceled Subscriptions</Card.Header>
        <Card.Body>
          <ListGroup variant="flush">
            {subscriptions.filter(s => s.status === 'CANCELED').length > 0 ? (
              subscriptions.filter(s => s.status === 'CANCELED').map(sub => (
                <ListGroup.Item key={sub.id} className="text-muted">
                  <strong>{sub.plan.planName}</strong> (Canceled)
                </ListGroup.Item>
              ))
            ) : (
              <p className="text-muted p-3">No canceled subscriptions.</p>
            )}
          </ListGroup>
        </Card.Body>
      </Card>

      {/* Cancel Confirmation Modal */}
      <Modal show={showModal} onHide={closeCancelModal}>
        <Modal.Header closeButton>
          <Modal.Title>Confirm Cancellation</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Are you sure you want to cancel your <strong>{selectedSub?.plan?.planName}</strong> subscription?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={closeCancelModal}>
            Close
          </Button>
          <Button variant="danger" onClick={handleCancel} disabled={cancelingId}>
            {cancelingId ? <Spinner as="span" animation="border" size="sm" /> : 'Confirm Cancel'}
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
}

export default MySubscriptions;
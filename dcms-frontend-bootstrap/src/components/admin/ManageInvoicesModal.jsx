import React, { useState, useEffect } from 'react';
import { Modal, Button, ListGroup, Form, Spinner, Alert, Row, Col } from 'react-bootstrap';
import { getCustomerInvoices, updateInvoiceDueDate } from '../../api/adminService';

function ManageInvoicesModal({ show, onHide, customer }) {
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [editState, setEditState] = useState({}); // Stores changes, e.g., { 1: "2025-11-04" }

  const fetchInvoices = async () => {
    if (!customer) return;
    try {
      setLoading(true);
      setError('');
      setSuccess('');
      const response = await getCustomerInvoices(customer.id);
      setInvoices(response.data);
      
      // Initialize edit state with current due dates
      const initialState = {};
      response.data.forEach(inv => {
        initialState[inv.id] = inv.dueDate;
      });
      setEditState(initialState);
    } catch (err) {
      setError('Failed to load invoices.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (show) {
      fetchInvoices();
    }
  }, [show]); // Refetch every time modal is opened

  const handleDateChange = (invoiceId, newDate) => {
    setEditState(prev => ({ ...prev, [invoiceId]: newDate }));
  };

  const handleUpdate = async (invoiceId) => {
    const newDate = editState[invoiceId];
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      await updateInvoiceDueDate(invoiceId, newDate);
      setSuccess(`Invoice ${invoiceId} due date updated!`);
      fetchInvoices(); // Refresh the list
    } catch (err) {
      setError('Failed to update due date.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal show={show} onHide={onHide} size="lg">
      <Modal.Header closeButton>
        <Modal.Title>Manage Invoices for {customer?.firstName}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {loading && !invoices.length && <div className="text-center"><Spinner animation="border" /></div>}
        {error && <Alert variant="danger">{error}</Alert>}
        {success && <Alert variant="success">{success}</Alert>}
        
        <ListGroup>
          {invoices.length === 0 && !loading && (
            <Alert variant="info">This customer has no invoices.</Alert>
          )}
          {invoices.map(inv => (
            <ListGroup.Item key={inv.id}>
              <Row className="align-items-center">
                <Col md={3}>
                  <strong>ID: {inv.id}</strong> (${inv.totalAmount.toFixed(2)})
                </Col>
                <Col md={3}>Status: {inv.status}</Col>
                <Col md={4}>
                  <Form.Control
                    type="date"
                    value={editState[inv.id] || ''}
                    onChange={(e) => handleDateChange(inv.id, e.target.value)}
                  />
                </Col>
                <Col md={2}>
                  <Button 
                    size="sm" 
                    onClick={() => handleUpdate(inv.id)}
                    disabled={loading}
                  >
                    {loading ? <Spinner as="span" size="sm" /> : 'Save'}
                  </Button>
                </Col>
              </Row>
            </ListGroup.Item>
          ))}
        </ListGroup>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          Close
        </Button>
      </Modal.Footer>
    </Modal>
  );
}

export default ManageInvoicesModal;
import React, { useState, useEffect } from 'react';
import { Table, Spinner, Alert, Badge, Card } from 'react-bootstrap';
import { getPaymentHistory } from '../api/customerService';

function PaymentHistory() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      setLoading(true);
      const response = await getPaymentHistory();
      setPayments(response.data);
    } catch (err) {
      setError('Failed to fetch payment history.');
    } finally {
      setLoading(false);
    }
  };

  const formatPaymentType = (type) => {
    if (type === 'TOP_UP') {
      return <Badge bg="success">Wallet Top-Up</Badge>;
    }
    if (type === 'INVOICE_PAYMENT') {
      return <Badge bg="primary">Bill Payment</Badge>;
    }
    return <Badge bg="secondary">{type}</Badge>;
  };

  const renderContent = () => {
    if (loading) {
      return <div className="text-center"><Spinner animation="border" /></div>;
    }

    if (error) {
      return <Alert variant="danger">{error}</Alert>;
    }

    if (payments.length === 0) {
      return <Alert variant="info">You have no payment history.</Alert>;
    }

    return (
      <Table striped bordered hover responsive>
        <thead>
          <tr>
            <th>Date</th>
            <th>Amount</th>
            <th>Type</th>
            <th>Source</th>
          </tr>
        </thead>
        <tbody>
          {payments.map(payment => (
            <tr key={payment.id}>
              <td>{new Date(payment.paymentDate).toLocaleString()}</td>
              <td>${payment.amount.toFixed(2)}</td>
              <td>{formatPaymentType(payment.type)}</td>
              <td>{payment.paymentSource}</td>
            </tr>
          ))}
        </tbody>
      </Table>
    );
  };

  return (
    <>
      <h2 className="mb-4">Payment History</h2>
      <Card className="shadow-sm">
        <Card.Body>
          {renderContent()}
        </Card.Body>
      </Card>
    </>
  );
}

export default PaymentHistory;
import React, { useState, useEffect } from 'react';
import { Card, Table, Spinner, Alert, Badge, Button } from 'react-bootstrap';
import { getCuredPayments } from '../../api/adminService';

function CuringPage() {
    const [curedPayments, setCuredPayments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchCuredPayments();
    }, []);

    const fetchCuredPayments = async () => {
        try {
            setLoading(true);
            const response = await getCuredPayments();
            setCuredPayments(response.data);
            setError('');
        } catch (err) {
            setError('Failed to fetch cured payments.');
        } finally {
            setLoading(false);
        }
    };

    const formatTimestamp = (timestamp) => {
        return new Date(timestamp).toLocaleDateString();
    };

    return (
        <>
            <h2 className="mb-4">Curing Workflow</h2>

            <Card className="shadow-sm">
                <Card.Header as="h5">Cured Payments</Card.Header>
                <Card.Body>
                    {loading && <div className="text-center"><Spinner animation="border" /></div>}
                    {error && <Alert variant="danger">{error}</Alert>}
                    {!loading && curedPayments.length === 0 && (
                         <Alert variant="info">No cured payments found yet.</Alert>
                    )}
                    {!loading && curedPayments.length > 0 && (
                        <Table hover responsive>
                            <thead>
                                <tr>
                                    <th>PAYMENT ID</th>
                                    <th>CUSTOMER NAME</th>
                                    <th>AMOUNT</th>
                                    <th>DATE</th>
                                    <th>STATUS</th>
                                </tr>
                            </thead>
                            <tbody>
                                {curedPayments.map(payment => (
                                    <tr key={payment.id}>
                                        <td>PAY00{payment.id}</td>
                                        <td>{payment.customerName}</td>
                                        <td>${payment.amount.toFixed(2)}</td>
                                        <td>{formatTimestamp(payment.paymentDate)}</td>
                                        <td>
                                            <Badge bg="success">Cured</Badge>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    )}
                </Card.Body>
            </Card>
        </>
    );
}

export default CuringPage;
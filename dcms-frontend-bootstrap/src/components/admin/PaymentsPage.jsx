import React, { useState, useEffect } from 'react';
import { Card, Table, Spinner, Alert, Badge, Row, Col } from 'react-bootstrap';
import { getAllPayments } from '../../api/adminService';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend } from 'chart.js';
import { Bar, Doughnut } from 'react-chartjs-2';

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend);

function PaymentsPage() {
    const [payments, setPayments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchPayments();
    }, []);

    const fetchPayments = async () => {
        try {
            setLoading(true);
            const response = await getAllPayments();
            setPayments(response.data);
            setError('');
        } catch (err) {
            setError('Failed to fetch payment data.');
        } finally {
            setLoading(false);
        }
    };

    const formatTimestamp = (timestamp) => {
        return new Date(timestamp).toLocaleDateString();
    };

    // --- Chart Data ---
    const { recoveryData, distributionData } = React.useMemo(() => {
        const topUps = payments.filter(p => p.type === 'TOP_UP');
        const invoicePayments = payments.filter(p => p.type === 'INVOICE_PAYMENT');

        const recoveryData = {
            labels: ['Invoice Payments', 'New Top-ups'],
            datasets: [
                {
                    data: [invoicePayments.length, topUps.length],
                    backgroundColor: ['#6d8ad1', '#d16d8a'],
                },
            ],
        };
        
        const distributionData = {
            labels: ['Prepaid', 'Postpaid'],
            datasets: [
                {
                    data: [
                        payments.filter(p => p.customerName.includes("Priya")).length, // Placeholder logic
                        payments.filter(p => !p.customerName.includes("Priya")).length, // Placeholder logic
                    ],
                    backgroundColor: ['#d1ad6d', '#6d8ad1'],
                },
            ],
        };
        return { recoveryData, distributionData };
    }, [payments]);

    return (
        <>
            <h2 className="mb-4">Payments Overview</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            
            <Row className="mb-4 g-4">
                <Col md={7}>
                    <Card className="shadow-sm h-100">
                        <Card.Header as="h5">Payment Recovery Trend</Card.Header>
                        <Card.Body>
                            <Bar 
                                data={recoveryData} 
                                options={{ responsive: true, plugins: { legend: { display: false } } }} 
                            />
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={5}>
                    <Card className="shadow-sm h-100">
                        <Card.Header as="h5">Customer Type Distribution</Card.Header>
                        <Card.Body>
                            <div style={{ maxWidth: '300px', margin: 'auto' }}>
                                <Doughnut 
                                    data={distributionData} 
                                    options={{ responsive: true, plugins: { legend: { position: 'right' } } }} 
                                />
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            <Card className="shadow-sm">
                <Card.Header as="h5">Recent Transactions</Card.Header>
                <Card.Body>
                    {loading && <div className="text-center"><Spinner animation="border" /></div>}
                    {!loading && payments.length === 0 && (
                        <Alert variant="info">No recent transactions found.</Alert>
                    )}
                    {!loading && payments.length > 0 && (
                        <Table hover responsive>
                            <thead>
                                <tr>
                                    <th>Transaction ID</th>
                                    <th>Customer Name</th>
                                    <th>Amount</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                    <th>Type</th>
                                </tr>
                            </thead>
                            <tbody>
                                {payments.map(p => (
                                    <tr key={p.id}>
                                        <td>PAY00{p.id}</td>
                                        <td>{p.customerName}</td>
                                        <td>${p.amount.toFixed(2)}</td>
                                        <td>{formatTimestamp(p.paymentDate)}</td>
                                        <td><Badge bg="success">Completed</Badge></td>
                                        <td>
                                            <Badge bg={p.type === 'TOP_UP' ? 'primary' : 'info'}>
                                                {p.type === 'TOP_UP' ? 'Top-up' : 'Invoice Payment'}
                                            </Badge>
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

export default PaymentsPage;
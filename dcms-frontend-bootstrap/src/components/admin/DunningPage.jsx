import React, { useState, useEffect } from 'react';
import { Card, Table, Spinner, Alert, Badge, Image } from 'react-bootstrap'; // <-- Button removed
import { getAllLogs } from '../../api/adminService';
import workflowChart from '../../assets/dunning.png'; // <-- Your image

function DunningPage() {
    const [logs, setLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchLogs();
    }, []);

    const fetchLogs = async () => {
        try {
            setLoading(true);
            const response = await getAllLogs();
            setLogs(response.data);
            setError('');
        } catch (err) {
            setError('Failed to fetch dunning logs.');
        } finally {
            setLoading(false);
        }
    };

    const formatTimestamp = (timestamp) => {
        return new Date(timestamp).toLocaleString();
    };

    const getStatusBadge = (event) => {
        if (!event) return <Badge bg="secondary">Unknown</Badge>; // Safety check
        if (event.includes('THROTTLED')) return <Badge bg="warning">Throttled</Badge>;
        if (event.includes('BLOCKED')) return <Badge bg="danger">Banned</Badge>;
        if (event.includes('NOTIFICATION')) return <Badge bg="info">Notified</Badge>;
        return <Badge bg="secondary">{event}</Badge>;
    };

    return (
        <>
            <h2 className="mb-4">Dunning Workflow</h2>

            {/* 1. Visualization (This part is correct) */}
            <Card className="shadow-sm mb-4">
                <Card.Header as="h5">Dunning Workflow Visualization</Card.Header>
                <Card.Body>
                    <Card.Text>
                        This is a static visualization of the dunning stages.
                    </Card.Text>
                    <Image 
                        src={workflowChart} 
                        fluid 
                        alt="Dunning workflow chart" 
                        className="border rounded" 
                    />
                </Card.Body>
            </Card>

            {/* 2. Simulation Log Table */}
            <Card className="shadow-sm">
                <Card.Header as="h5">Simulation Log</Card.Header>
                <Card.Body>
                    {loading && <div className="text-center"><Spinner animation="border" /></div>}
                    {error && <Alert variant="danger">{error}</Alert>}
                    {!loading && !error && (
                        <Table hover responsive>
                            <thead>
                                <tr>
                                    <th>Log ID</th>
                                    <th>Timestamp</th>
                                    <th>Customer ID</th>
                                    <th>Customer Name</th>
                                    <th>Status / Action</th>
                                    <th>Details</th>
                                    {/* <th>Actions</th> <-- REMOVED */}
                                </tr>
                            </thead>
                            <tbody>
                                {logs.map(log => (
                                    <tr key={log.id}>
                                        <td>SIM00{log.id}</td>
                                        <td>{formatTimestamp(log.eventTimestamp)}</td>
                                        
                                        {/* --- THIS IS THE FIX --- */}
                                        <td>CUST00{log.customerId}</td>
                                        <td>{log.customerName}</td>
                                        {/* --- END OF FIX --- */}
                                        
                                        <td>{getStatusBadge(log.eventType)}</td>
                                        <td>{log.details}</td>
                                        
                                        {/* <td>...</td> <-- REMOVED */}
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

export default DunningPage;
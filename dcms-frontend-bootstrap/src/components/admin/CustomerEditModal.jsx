import React, { useState, useEffect } from 'react';
import { Modal, Button, Form, Row, Col, Alert } from 'react-bootstrap';
// --- IMPORT THE NEW MODAL ---
import ManageInvoicesModal from './ManageInvoicesModal';

const SEGMENTS = ["PREPAID", "POSTPAID"];
const STATUSES = ["ACTIVE", "INACTIVE", "THROTTLED", "BLOCKED"];

// Use your existing props
function CustomerEditModal({ show, handleClose, handleSave, customer }) {
    const [formData, setFormData] = useState({});
    const [error, setError] = useState('');
    const [showInvoiceModal, setShowInvoiceModal] = useState(false); // This is correct

    // Load customer data into form when the modal opens
    useEffect(() => {
        if (customer) {
            setFormData({
                ...customer,
                // Ensure date is in YYYY-MM-DD format for the input
                dueDate: customer.dueDate ? customer.dueDate.split('T')[0] : '' // Use empty string for <Form.Control>
            });
        }
    }, [customer, show]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const onSave = () => {
        // Simple validation
        if (!formData.firstName || !formData.email) {
            setError('First Name and Email are required.');
            return;
        }
        
        // Convert empty string back to null for the backend
        const dataToSave = {
            ...formData,
            dueDate: formData.dueDate === '' ? null : formData.dueDate
        };
        
        handleSave(dataToSave);
    };

    // Close handler for the new modal
    const closeInvoiceModal = () => {
        setShowInvoiceModal(false);
        // We must also refresh the main modal's data in case the due date changed
        handleClose(); // This will trigger CustomersPage to refetch
    }

    return (
        <>
            <Modal show={show} onHide={handleClose} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>Edit Customer: {customer?.firstName} {customer?.lastName}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {error && <Alert variant="danger">{error}</Alert>}
                    
                    {/* --- THIS IS THE NEW SECTION --- */}
                    <Alert variant="info">
                        <Alert.Heading>Invoice Management</Alert.Heading>
                        <p>
                            To change the customer's due date for dunning, you must edit their invoice directly.
                        </p>
                        <Button 
                            variant="primary" 
                            className="w-100"
                            onClick={() => setShowInvoiceModal(true)}
                        >
                            Manage Customer Invoices
                        </Button>
                    </Alert>
                    {/* --- END OF NEW SECTION --- */}

                    <Form>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>First Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="firstName"
                                        value={formData.firstName || ''}
                                        onChange={handleChange}
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Last Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="lastName"
                                        value={formData.lastName || ''}
                                        onChange={handleChange}
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row>
                            <Col md={7}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Email</Form.Label>
                                    <Form.Control
                                        type="email"
                                        name="email"
                                        value={formData.email || ''}
                                        onChange={handleChange}
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={5}>
                                 <Form.Group className="mb-3">
                                    <Form.Label>Phone</Form.Label>
                                    <Form.Control
                                        type="tel"
                                        name="phone"
                                        value={formData.phone || ''}
                                        onChange={handleChange}
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <hr />
                        <Row>
                            <Col md={4}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Customer Segment</Form.Label>
                                    <Form.Select name="segment" value={formData.segment || ''} onChange={handleChange}>
                                        {SEGMENTS.map(s => <option key={s} value={s}>{s}</option>)}
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                             <Col md={4}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Service Status</Form.Label>
                                    <Form.Select name="status" value={formData.status || ''} onChange={handleChange}>
                                        {STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                             <Col md={4}>
                                {/* --- THIS IS THE UPDATED DUE DATE FIELD --- */}
                                <Form.Group className="mb-3">
                                    <Form.Label>Due Date (Read-only)</Form.Label>
                                    <Form.Control
                                        type="date"
                                        name="dueDate"
                                        value={formData.dueDate || ''}
                                        onChange={handleChange}
                                        readOnly
                                        disabled
                                    />
                                    <Form.Text className="text-muted">
                                        This date is set automatically. Use "Manage Invoices" to change it.
                                    </Form.Text>
                                </Form.Group>
                                {/* --- END OF UPDATE --- */}
                            </Col>
                        </Row>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Cancel
                    </Button>
                    <Button variant="primary" onClick={onSave}>
                        Save Changes
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* --- THIS IS THE NEW MODAL COMPONENT --- */}
            {/* It renders, but is hidden until showInvoiceModal is true */}
            <ManageInvoicesModal 
                show={showInvoiceModal}
                onHide={closeInvoiceModal}
                customer={customer}
            />
            {/* --- END OF NEW MODAL COMPONENT --- */}
        </>
    );
}

export default CustomerEditModal;
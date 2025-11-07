import React, { useState, useEffect } from 'react';
import { Modal, Button, Form, Row, Col, Alert } from 'react-bootstrap';

// Get enums for dropdowns
const SEGMENTS = ["POSTPAID", "PREPAID", "ALL"];
const ACTIONS = [
    "SEND_SMS", 
    "SEND_EMAIL", 
    "NOTIFY_THROTTLE", 
    "THROTTLE_DATA", 
    "BLOCK_VOICE", 
    "BLOCK_ALL_SERVICES"
];

const emptyRule = {
    ruleName: '',
    minOverdueDays: 0,
    maxOverdueDays: 0,
    targetSegment: 'POSTPAID',
    actionToTake: 'SEND_SMS',
    active: true // <-- RENAMED
};

function RuleModal({ show, handleClose, handleSave, initialData }) {
    const [formData, setFormData] = useState(emptyRule);
    const [error, setError] = useState('');

    // When the 'initialData' prop changes, update the form
    useEffect(() => {
        if (initialData) {
            setFormData(initialData);
        } else {
            setFormData(emptyRule);
        }
        setError(''); // Clear error when modal opens
    }, [initialData, show]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value
        });
    };

    const onSave = () => {
        if (!formData.ruleName) {
            setError('Rule Name is required.');
            return;
        }
        if (formData.minOverdueDays >= formData.maxOverdueDays) {
            setError('Max Days must be greater than Min Days.');
            return;
        }
        handleSave(formData);
    };

    return (
        <Modal show={show} onHide={handleClose} size="lg">
            <Modal.Header closeButton>
                <Modal.Title>{initialData ? 'Edit Rule' : 'Add New Rule'}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {error && <Alert variant="danger">{error}</Alert>}
                <Form>
                    {/* ... (All other form groups: ruleName, min/max days, segment, action) ... */}

                    <Form.Group className="mb-3" controlId="ruleName">
                        <Form.Label>Rule Name</Form.Label>
                        <Form.Control
                            type="text"
                            name="ruleName"
                            value={formData.ruleName}
                            onChange={handleChange}
                            placeholder="e.g., Postpaid Day 5 Throttle"
                        />
                    </Form.Group>
                    
                    <Row>
                        <Col>
                            <Form.Group className="mb-3" controlId="minOverdueDays">
                                <Form.Label>Min Overdue Days</Form.Label>
                                <Form.Control
                                    type="number"
                                    name="minOverdueDays"
                                    value={formData.minOverdueDays}
                                    onChange={handleChange}
                                />
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="mb-3" controlId="maxOverdueDays">
                                <Form.Label>Max Overdue Days</Form.Label>
                                <Form.Control
                                    type="number"
                                    name="maxOverdueDays"
                                    value={formData.maxOverdueDays}
                                    onChange={handleChange}
                                />
                            </Form.Group>
                        </Col>
                    </Row>
                    
                    <Row>
                        <Col>
                            <Form.Group className="mb-3" controlId="targetSegment">
                                <Form.Label>Customer Segment</Form.Label>
                                <Form.Select name="targetSegment" value={formData.targetSegment} onChange={handleChange}>
                                    {SEGMENTS.map(s => <option key={s} value={s}>{s}</option>)}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="mb-3" controlId="actionToTake">
                                <Form.Label>Action to Take</Form.Label>
                                <Form.Select name="actionToTake" value={formData.actionToTake} onChange={handleChange}>
                                    {ACTIONS.map(a => <option key={a} value={a}>{a}</option>)}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                    </Row>
                    
                    {/* --- THIS IS THE FIX --- */}
                    <Form.Check
                        type="switch"
                        id="active" // <-- RENAMED
                        label="Rule is Active"
                        name="active" // <-- RENAMED
                        checked={formData.active} // <-- RENAMED
                        onChange={handleChange}
                    />
                    {/* --- END OF FIX --- */}
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
    );
}

export default RuleModal;
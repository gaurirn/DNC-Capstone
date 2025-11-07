import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Spinner, Alert, Badge } from 'react-bootstrap';
import { getAllRules, createRule, updateRule, deleteRule } from '../../api/adminService';
import RuleModal from './RuleModal'; // Import the modal

function RulesPage() {
    const [rules, setRules] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    
    // Modal state
    const [showModal, setShowModal] = useState(false);
    const [currentRule, setCurrentRule] = useState(null); // null for 'Add', or a rule object for 'Edit'

    useEffect(() => {
        fetchRules();
    }, []);

    const fetchRules = async () => {
        try {
            setLoading(true);
            const response = await getAllRules();
            setRules(response.data);
            setError('');
        } catch (err) {
            setError('Failed to fetch rules.');
        } finally {
            setLoading(false);
        }
    };

    // --- Modal and CRUD Handlers (Unchanged) ---

    const handleOpenAddModal = () => {
        setCurrentRule(null);
        setShowModal(true);
    };

    const handleOpenEditModal = (rule) => {
        setCurrentRule(rule);
        setShowModal(true);
    };

    const handleCloseModal = () => {
        setShowModal(false);
        setCurrentRule(null);
    };

    const handleSave = async (ruleData) => {
        try {
            if (ruleData.id) {
                // Update existing rule
                await updateRule(ruleData.id, ruleData);
            } else {
                // Create new rule
                await createRule(ruleData);
            }
            fetchRules(); // Refresh list
            handleCloseModal();
        } catch (err) {
            setError('Failed to save rule.');
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this rule?')) {
            try {
                await deleteRule(id);
                fetchRules(); // Refresh list
            } catch (err) {
                setError('Failed to delete rule.');
            }
        }
    };

    return (
        <>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>Rule Management</h2>
                <Button variant="primary" onClick={handleOpenAddModal}>
                    Add Rule
                </Button>
            </div>

            {loading && <div className="text-center"><Spinner animation="border" /></div>}
            {error && <Alert variant="danger">{error}</Alert>}

            <Row xs={1} md={2} lg={3} className="g-4">
                {!loading && rules.map(rule => (
                    <Col key={rule.id}>
                        <Card className="shadow-sm h-100">
                            <Card.Body>
                                <Card.Title className="d-flex justify-content-between">
                                    {rule.ruleName}
                                    
                                    {/* --- THIS IS THE FIX --- */}
                                    <Badge bg={rule.active ? 'success' : 'secondary'}>
                                        {rule.active ? 'Active' : 'Inactive'}
                                    </Badge>
                                    {/* --- END OF FIX --- */}

                                </Card.Title>
                                <Card.Text>
                                    <strong>Days Overdue:</strong> {rule.minOverdueDays} - {rule.maxOverdueDays}
                                    <br />
                                    <strong>Segment:</strong> {rule.targetSegment}
                                    <br />
                                    <strong>Action:</strong> {rule.actionToTake}
                                </Card.Text>
                            </Card.Body>
                            <Card.Footer>
                                <Button variant="outline-secondary" size="sm" onClick={() => handleOpenEditModal(rule)}>
                                    Edit
                                </Button>
                                <Button variant="danger" size="sm" className="ms-2" onClick={() => handleDelete(rule.id)}>
                                    Delete
                                </Button>
                            </Card.Footer>
                        </Card>
                    </Col>
                ))}
            </Row>

            {/* The Modal for Adding/Editing */}
            <RuleModal
                show={showModal}
                handleClose={handleCloseModal}
                handleSave={handleSave}
                initialData={currentRule}
            />
        </>
    );
}

export default RulesPage;
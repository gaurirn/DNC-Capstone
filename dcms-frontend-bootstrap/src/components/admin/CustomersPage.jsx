import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllCustomers, updateCustomer } from '../../api/adminService'; // <-- Import updateCustomer
import { Table, Form, Button, Badge, Card, Row, Col, InputGroup, Spinner, Alert } from 'react-bootstrap';
import { Search } from 'react-bootstrap-icons';
import CustomerEditModal from './CustomerEditModal'; // <-- IMPORT THE NEW MODAL

function CustomersPage() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  // --- NEW STATE FOR MODAL ---
  const [showModal, setShowModal] = useState(false);
  const [currentCustomer, setCurrentCustomer] = useState(null);
  
  // const navigate = useNavigate(); // No longer needed if "View" is "Edit"

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      const response = await getAllCustomers();
      setCustomers(response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch customers. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    // ... (this function is unchanged)
    switch(status) {
      case 'ACTIVE': return <Badge bg="success">Active</Badge>;
      case 'THROTTLED': return <Badge bg="warning">Throttled</Badge>;
      case 'BLOCKED': return <Badge bg="danger">Banned</Badge>;
      case 'INACTIVE': return <Badge bg="secondary">Inactive</Badge>;
      default: return <Badge bg="secondary">{status}</Badge>;
    }
  };

  // --- NEW HANDLERS FOR MODAL ---
  const handleOpenEditModal = (customer) => {
    setCurrentCustomer(customer);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setCurrentCustomer(null);
  };

  const handleSaveCustomer = async (customerData) => {
    try {
      await updateCustomer(customerData.id, customerData);
      handleCloseModal();
      fetchCustomers(); // Refresh the customer list
    } catch (err) {
      setError('Failed to update customer. Please try again.');
    }
  };
  
  // --- (filteredCustomers logic is unchanged) ---
  const filteredCustomers = customers.filter(customer => {
    const matchesStatus = statusFilter ? customer.status === statusFilter : true;
    const searchTermLower = searchTerm.toLowerCase();
    const matchesSearch = (customer.firstName?.toLowerCase() || '').includes(searchTermLower) ||
                          (customer.lastName?.toLowerCase() || '').includes(searchTermLower) ||
                          (customer.email?.toLowerCase() || '').includes(searchTermLower);
    return matchesStatus && matchesSearch;
  });

  return (
    <>
      <h2 className="mb-4">Customers Overview</h2>
      
      {/* Filter Card (unchanged) */}
      <Card className="shadow-sm mb-4">
        {/* ... (all filter logic) ... */}
      </Card>

      {/* Customer List Card */}
      <Card className="shadow-sm">
        <Card.Header as="h5" className="d-flex justify-content-between align-items-center">
          Customer List
          {/* <Button variant="outline-primary" size="sm">Add Customer</Button> <-- REMOVED */}
        </Card.Header>
        <Card.Body>
          {loading && <div className="text-center"><Spinner animation="border" /></div>}
          {error && <Alert variant="danger">{error}</Alert>}
          {!loading && !error && (
            <Table hover responsive>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Status</th>
                  <th>Balance</th>
                  <th>Amount Due</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredCustomers.map(customer => (
                  <tr key={customer.id}>
                    <td>CUST00{customer.id}</td>
                    <td>{customer.firstName} {customer.lastName}</td>
                    <td>{customer.email}</td>
                    <td>{getStatusBadge(customer.status)}</td>
                    <td>${customer.balance?.toFixed(2) || '0.00'}</td>
                    <td>${customer.amountOverdue?.toFixed(2) || '0.00'}</td>
                    <td>
                      {/* --- UPDATED BUTTON --- */}
                      <Button 
                        variant="outline-primary" 
                        size="sm"
                        onClick={() => handleOpenEditModal(customer)}
                      >
                        Edit
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </Card.Body>
      </Card>

      {/* --- ADD THE MODAL COMPONENT --- */}
      {currentCustomer && (
        <CustomerEditModal
          show={showModal}
          handleClose={handleCloseModal}
          handleSave={handleSaveCustomer}
          customer={currentCustomer}
        />
      )}
    </>
  );
}

export default CustomersPage;
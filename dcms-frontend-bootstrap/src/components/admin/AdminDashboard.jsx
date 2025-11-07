import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Spinner, Alert, ListGroup, Badge } from 'react-bootstrap';
import { 
  getDashboardStats, 
  getTopRiskAccounts,
  triggerInvoiceCreation, 
  triggerBillingCycle,
  triggerDunning,
  triggerUsage
} from '../../api/adminService';

// Import Chart.js components
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Doughnut } from 'react-chartjs-2';

// Register Chart.js components
ChartJS.register(ArcElement, Tooltip, Legend);


// StatCard component
function StatCard({ title, value, color }) {
  return (
    <Card className={`text-white shadow`} style={{ backgroundColor: color }}>
      <Card.Body>
        <Card.Title as="h5">{title}</Card.Title>
        <Card.Text as="h2" className="fw-bold">
          {value}
        </Card.Text>
      </Card.Body>
    </Card>
  );
}

// TriggerButton component
function TriggerButton({ title, apiCall, variant, onUpdate }) {
  const [loading, setLoading] = useState(false);
  const handleClick = async () => {
    setLoading(true);
    try {
      const response = await apiCall();
      onUpdate('success', response.data.message);
    } catch (err) {
      onUpdate('danger', `Error: ${err.response?.data?.message || err.message}`);
    }
    setLoading(false);
  };
  return (
    <Button variant={variant} onClick={handleClick} disabled={loading} className="w-100">
      {loading ? <Spinner as="span" animation="border" size="sm" /> : title}
    </Button>
  );
}

function AdminDashboard() {
  const [stats, setStats] = useState({
    totalCustomers: 0,
    totalRecoveredRevenue: 0,
    accountsThrottled: 0,
    accountsBlocked: 0,
    accountsInactive: 0 // <-- ADDED
  });
  const [topAccounts, setTopAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [alert, setAlert] = useState({ show: false, variant: '', message: '' });

  // Fetch stats and top accounts on load
  useEffect(() => {
    const fetchAllData = async () => {
      setLoading(true);
      try {
        // Fetch stats and top accounts in parallel
        const [statsResponse, topAccountsResponse] = await Promise.all([
            getDashboardStats(),
            getTopRiskAccounts()
        ]);
        
        setStats(statsResponse.data);
        setTopAccounts(topAccountsResponse.data);

      } catch (err) {
        setAlert({ show: true, variant: 'danger', message: 'Failed to load dashboard data.' });
      }
      setLoading(false);
    };
    fetchAllData();
  }, []);

  const handleTriggerUpdate = (variant, message) => {
    setAlert({ show: true, variant, message });
    setTimeout(() => setAlert({ show: false, variant: '', message: '' }), 5000);
  };

  // --- UPDATED CHART DATA ---
  
  // Calculate active customers
  const activeCustomers = stats.totalCustomers - stats.accountsThrottled - stats.accountsBlocked - stats.accountsInactive;

  const chartData = {
    labels: ['Active', 'Throttled', 'Banned', 'Inactive'], // <-- ADDED
    datasets: [
      {
        data: [
            activeCustomers > 0 ? activeCustomers : 0, // Ensure no negative numbers
            stats.accountsThrottled, 
            stats.accountsBlocked, 
            stats.accountsInactive // <-- ADDED
        ],
        backgroundColor: [
          '#6d8ad1', // Blue (Active)
          '#d1ad6d', // Yellow (Throttled)
          '#d16d6d', // Red (Banned)
          '#aaaaaa', // Grey (Inactive) <-- ADDED
        ],
        borderColor: [
          '#ffffff',
          '#ffffff',
          '#ffffff',
          '#ffffff', // <-- ADDED
        ],
        borderWidth: 1,
      },
    ],
  };
  
  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Customer Status Breakdown',
      },
    },
  };
  // --- END OF UPDATES ---

  if (loading) {
    return <div className="text-center"><Spinner animation="border" /></div>;
  }

  return (
    <>
      <h2 className="mb-4">Admin Dashboard</h2>

      {alert.show && (
        <Alert variant={alert.variant} onClose={() => setAlert({ show: false })} dismissible>
          {alert.message}
        </Alert>
      )}

      {/* Stats Row */}
      <Row className="mb-4 g-4"> {/* g-4 adds gaps */}
        <Col md={3}>
          <StatCard title="Total Customers" value={stats.totalCustomers} color="#d16d8a" />
        </Col>
        <Col md={3}>
          <StatCard title="Total Revenue" value={`$${stats.totalRecoveredRevenue.toFixed(2)}`} color="#6d8ad1" />
        </Col>
        <Col md={3}>
          <StatCard title="Throttled" value={stats.accountsThrottled} color="#d1ad6d" />
        </Col>
        <Col md={3}>
          <StatCard title="Banned" value={stats.accountsBlocked} color="#d16d6d" />
        </Col>
      </Row>
      
      {/* --- CORRECTED LAYOUT ROW --- */}
      <Row className="g-4">
        {/* Left Column (Chart) */}
        <Col md={7}>
          <Card className="shadow-sm h-100">
            <Card.Header as="h5">Dunning Funnel</Card.Header>
            <Card.Body>
              <p>This chart shows the breakdown of all customers by their current status.</p>
              <div style={{ maxWidth: '400px', margin: 'auto' }}>
                <Doughnut data={chartData} options={chartOptions} />
              </div>
            </Card.Body>
          </Card>
        </Col>

        {/* Right Column (Lists & Triggers) */}
        <Col md={5}>
          {/* Top 5 Accounts Card */}
          <Card className="shadow-sm mb-4">
            <Card.Header as="h5">Top 5 At-Risk Accounts</Card.Header>
            <Card.Body>
              <ListGroup variant="flush">
                {topAccounts.length > 0 ? (
                  topAccounts.map(cust => (
                    <ListGroup.Item key={cust.id} className="d-flex justify-content-between align-items-center">
                      <div>
                        <div className="fw-bold">Cust ID: #{cust.id}</div>
                        <small>Amount Due: ${cust.amountOverdue?.toFixed(2) || '0.00'}</small>
                      </div>
                      <Badge bg={cust.status === 'BLOCKED' ? 'danger' : 'warning'} pill>
                        {cust.status === 'BLOCKED' ? 'Banned' : 'Throttled'}
                      </Badge>
                    </ListGroup.Item>
                  ))
                ) : (
                  <p className="text-muted">No at-risk accounts found.</p>
                )}
              </ListGroup>
            </Card.Body>
          </Card>

          {/* Triggers Card */}
          <Card className="shadow-sm">
            <Card.Header as="h5">System Triggers</Card.Header>
            <Card.Body>
              <Card.Text>Manually run system billing and dunning cycles.</Card.Text>
              <Row className="g-2">
                <Col xs={12}>
                  <TriggerButton 
                    title="Force Invoice Creation" 
                    apiCall={triggerInvoiceCreation} 
                    variant="primary"
                    onUpdate={handleTriggerUpdate} 
                  />
                </Col>
                <Col xs={12}>
                  <TriggerButton 
                    title="Update Billing Statuses" 
                    apiCall={triggerBillingCycle} 
                    variant="info"
                    onUpdate={handleTriggerUpdate} 
                  />
                </Col>
                <Col xs={12}>
                  <TriggerButton 
                    title="Run Dunning Engine" 
                    apiCall={triggerDunning} 
                    variant="warning"
                    onUpdate={handleTriggerUpdate} 
                  />
                </Col>
                <Col xs={12}>
                  <TriggerButton 
                    title="Simulate Data Usage" 
                    apiCall={triggerUsage} 
                    variant="secondary"
                    onUpdate={handleTriggerUpdate} 
                  />
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </>
  );
}

export default AdminDashboard;
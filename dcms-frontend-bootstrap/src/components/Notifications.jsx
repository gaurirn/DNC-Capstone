import React, { useState, useEffect } from 'react';
import { Spinner, Alert, ListGroup, Button, Row, Col } from 'react-bootstrap';
import { getMyNotifications } from '../api/customerService';
import { CheckCircleFill, ExclamationTriangleFill, InfoCircleFill, BellFill } from 'react-bootstrap-icons';
import { useNavigate } from 'react-router-dom';

// --- THIS IS THE FIX ---
// Helper to format the timestamp to (hrs:min:sec dd/mm/yyyy)
const formatTimestamp = (dateString) => {
  const date = new Date(dateString);
  
  // Check if the date is valid
  if (isNaN(date.getTime())) {
    return "Invalid Date"; // Fallback
  }

  // Helper function to pad numbers with a leading zero
  const pad = (num) => String(num).padStart(2, '0');

  const day = pad(date.getDate()); // dd
  const month = pad(date.getMonth() + 1); // MM (getMonth is 0-indexed)
  const year = date.getFullYear(); // yyyy

  const hours = pad(date.getHours()); // HH
  const minutes = pad(date.getMinutes()); // mm
  const seconds = pad(date.getSeconds()); // ss

  return `(${hours}:${minutes}:${seconds} ${day}/${month}/${year})`;
};
// --- END OF FIX ---

// Helper to get an icon and color based on the event type
const getNotificationIcon = (eventType) => {
  // These eventTypes match your DunningEventLog entity
  switch (eventType) {
    case 'CURED':
      return <CheckCircleFill className="me-3" color="#198754" size={24} />; // Green
    case 'BLOCKED':
    case 'THROTTLED':
      return <ExclamationTriangleFill className="me-3" color="#DC3545" size={24} />; // Red
    case 'NOTIFICATION_SENT':
      return <InfoCircleFill className="me-3" color="#0D6EFD" size={24} />; // Blue
    default:
      return <BellFill className="me-3" color="#6C757D" size={24} />; // Gray
  }
};

function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const response = await getMyNotifications();
      setNotifications(response.data);
    } catch (err) {
      setError('Failed to fetch notifications.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * This is the "translation" function.
   * It converts a system log (eventType) into a user-friendly message.
   */
  const getNotificationDetails = (notif) => {
    const { eventType, message } = notif;
    let details = {
      icon: <BellFill className="me-3" color="gray" size={24} />,
      message: message, // Default
      action: null
    };

    switch (eventType) {
      case 'THROTTLED':
        details.icon = <ExclamationTriangleFill className="me-3" color="#DC3545" size={24} />; // Red
        details.message = "Your account is THROTTLED now. Please pay your outstanding bill to prevent it from being BLOCKED.";
        details.action = <Button variant="primary" size="sm" onClick={() => navigate('/dashboard')}>Pay Now</Button>;
        break;
      
      case 'BLOCKED':
        details.icon = <ExclamationTriangleFill className="me-3" color="#DC3545" size={24} />; // Red
        details.message = "Your account has been BANNED. Please pay your outstanding bill to restore service.";
        details.action = <Button variant="primary" size="sm" onClick={() => navigate('/dashboard')}>Pay Now</Button>;
        break;
      
      case 'CURED':
        details.icon = <CheckCircleFill className="me-3" color="#198754" size={24} />; // Green
        details.message = "Good news! Your service has been fully restored. Enjoy uninterrupted connectivity.";
        details.action = <Button variant="link" size="sm" onClick={() => navigate('/dashboard')}>View Dashboard</Button>;
        break;
      
      case 'NOTIFICATION_SENT':
        details.icon = <InfoCircleFill className="me-3" color="#0D6EFD" size={24} />; // Blue
        // Use the specific message from the dunning engine
        details.message = message; 
        details.action = <Button variant="primary" size="sm" onClick={() => navigate('/dashboard')}>Pay Now</Button>;
        break;
      
      default:
        // For other events like 'BALANCE_ADDED', etc.
        details.icon = <InfoCircleFill className="me-3" color="#6C757D" size={24} />;
        details.message = message;
        break;
    }
    
    return details;
  };

  const renderContent = () => {
    if (loading) {
      return <div className="text-center p-5"><Spinner animation="border" /></div>;
    }

    if (error) {
      return <Alert variant="danger">{error}</Alert>;
    }

    if (notifications.length === 0) {
      return <Alert variant="info" className="text-center m-4">You have no notifications.</Alert>;
    }

    // This renders the list based on your mockup
    return (
      <ListGroup variant="flush">
        {notifications.map(notif => {
          // Get the translated, friendly details
          const details = getNotificationDetails(notif);
          
          return (
            <ListGroup.Item key={notif.id} className="p-4" style={{ backgroundColor: 'white' }}>
              <Row>
                <Col xs="auto" className="pt-1">
                  {details.icon}
                </Col>
                <Col>
                  <div className="d-flex justify-content-between">
                    <span className="text-muted" style={{ fontSize: '0.9rem' }}>
                      {formatTimestamp(notif.timestamp)}
                    </span>
                    {/* (Three-dot menu can be added here later) */}
                  </div>
                  <p className="my-2" style={{ fontSize: '1.1rem' }}>
                    {details.message}
                  </p>
                  {details.action}
                </Col>
              </Row>
            </ListGroup.Item>
          );
        })}
      </ListGroup>
    );
  };

  return (
    <>
      <h2 className="mb-4">Notifications</h2>
      {/* Wrap in a Card for the border, like the mockup */}
      <div className="card shadow-sm" style={{ border: 'none' }}>
        {renderContent()}
      </div>
    </>
  );
}

export default Notifications;
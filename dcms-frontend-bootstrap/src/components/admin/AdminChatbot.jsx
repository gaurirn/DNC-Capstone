import React, { useState, useRef, useEffect } from 'react';
import { Card, Form, Button, Spinner, CloseButton } from 'react-bootstrap';
import { sendAdminChatMessage } from '../../api/adminService'; // <-- Admin API
import { ChatDots, SendFill, X } from 'react-bootstrap-icons';
import '../Chatbot.css'; // <-- Re-use the shared CSS (note the '../')

function AdminChatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { from: 'ai', text: "Hello, Admin. How can I help you analyze the system today?" }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!input.trim()) return;

    const userMessage = { from: 'user', text: input };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setLoading(true);

    try {
      // Calls the new function from adminService.js
      const response = await sendAdminChatMessage(input); 
      const aiMessage = { from: 'ai', text: response.data.response };
      setMessages(prev => [...prev, aiMessage]);
    } catch (err) {
      const errorMsg = { from: 'ai', text: "Sorry, I'm having trouble connecting. Please try again." };
      setMessages(prev => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <div className={`chat-widget-container ${isOpen ? 'open' : ''}`}>
        <Card className="shadow-lg">
          {/* Admin Theme: Dark Header */}
          <Card.Header className="d-flex justify-content-between align-items-center bg-dark text-white">
            <h5 className="mb-0">Admin AI Assistant</h5>
            <CloseButton variant="white" onClick={() => setIsOpen(false)} />
          </Card.Header>
          <Card.Body className="p-0">
            <div className="chat-message-list">
              {messages.map((msg, index) => (
                <div key={index} className={`chat-message ${msg.from}`}>
                  <div className="message-bubble">{msg.text}</div>
                </div>
              ))}
              {loading && (
                <div className="chat-message ai">
                  <Spinner animation="border" size="sm" className="ms-2" />
                </div>
              )}
              <div ref={messagesEndRef} />
            </div>
          </Card.Body>
          <Form onSubmit={handleSubmit} className="chat-input-form d-flex">
            <Form.Control
              type="text"
              placeholder="e.g., find customer 'ramesh@test.com'"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              disabled={loading}
              className="me-2"
            />
            {/* Admin Theme: Dark Button */}
            <Button variant="dark" type="submit" disabled={loading}>
              <SendFill />
            </Button>
          </Form>
        </Card>
      </div>
      
      {/* Admin Theme: Dark Bubble */}
      <button
        className="chat-bubble-toggle"
        style={{ backgroundColor: '#343a40' }} 
        onClick={() => setIsOpen(prev => !prev)}
      >
        {isOpen ? <X size={28} /> : <ChatDots size={28} />}
      </button>
    </>
  );
}

export default AdminChatbot;
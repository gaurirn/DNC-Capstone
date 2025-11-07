import React, { useState, useRef, useEffect } from 'react';
import { Card, Form, Button, Spinner, CloseButton } from 'react-bootstrap';
import { sendChatMessage } from '../api/customerService'; // <-- Customer API
import { ChatDots, SendFill, X } from 'react-bootstrap-icons';
import './Chatbot.css'; // Import the shared CSS

function CustomerChatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { from: 'ai', text: "Hello! How can I help with your account today?" }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null); // To auto-scroll

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
      const response = await sendChatMessage(input); // <-- Call customer service
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
        <Card className="h-100 shadow-lg">
          <Card.Header className="d-flex justify-content-between align-items-center bg-primary text-white">
            <h5 className="mb-0">AI Assistant</h5>
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
              placeholder="Ask a question..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              disabled={loading}
              className="me-2"
            />
            <Button variant="primary" type="submit" disabled={loading}>
              <SendFill />
            </Button>
          </Form>
        </Card>
      </div>
      <button
        className="chat-bubble-toggle"
        onClick={() => setIsOpen(prev => !prev)}
      >
        {isOpen ? <X size={28} /> : <ChatDots size={28} />}
      </button>
    </>
  );
}

export default CustomerChatbot;
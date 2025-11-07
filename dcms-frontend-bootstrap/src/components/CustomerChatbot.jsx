import React, { useState, useEffect, useRef } from 'react';
import { Card, Form, Button, Spinner, CloseButton, Modal } from 'react-bootstrap';
import { ChatDots, SendFill, Trash, X } from 'react-bootstrap-icons';
import { sendCustomerChatMessage, clearCustomerChatHistory } from '../api/customerService';
import './Chatbot.css'; 

function CustomerChatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { from: 'ai', text: 'Hi — I can help with your account, billing, and subscriptions. Ask me anything about your invoices or plan.' }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [chatId, setChatId] = useState(() => localStorage.getItem('customerChatId') || null);
  const [showClearConfirm, setShowClearConfirm] = useState(false);

  const messagesEndRef = useRef(null);

  // autoscroll
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
  }, [messages, loading]);

  // Save chatId updates to localStorage so subsequent replies can reuse it (optional)
  useEffect(() => {
    if (chatId) localStorage.setItem('customerChatId', chatId);
  }, [chatId]);

  const addMessage = (from, text) => {
    setMessages(prev => [...prev, { from, text }]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const trimmed = input.trim();
    if (!trimmed) return;

    // push user message locally immediately
    addMessage('user', trimmed);
    setInput('');
    setLoading(true);

    try {
      // send to backend; include chatId if present (backend ignores or uses it)
      const response = await sendCustomerChatMessage(trimmed, chatId);

      // backend returns ChatResponseDto { message: "...", chatId: "..." }
      const resData = response?.data || {};

      const aiText = resData.message || resData.response || "Sorry — I couldn't understand that.";
      const returnedChatId = resData.chatId || resData.id || null;

      if (returnedChatId && !chatId) {
        setChatId(returnedChatId);
      }

      addMessage('ai', aiText);
    } catch (err) {
      console.error('Customer chat error:', err);
      // show error to user
      const errMsg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        'Sorry — failed to connect to the chat service. Please try again.';
      addMessage('ai', errMsg);
    } finally {
      setLoading(false);
    }
  };

  const handleClearHistory = async () => {
    setShowClearConfirm(false);
    setLoading(true);
    try {
      await clearCustomerChatHistory();
      // reset local messages (keep a small greeting)
      setMessages([{ from: 'ai', text: 'Chat history cleared. How can I help you now?' }]);
      setChatId(null);
      localStorage.removeItem('customerChatId');
    } catch (err) {
      console.error('Failed to clear chat history:', err);
      addMessage('ai', 'Sorry — could not clear chat history. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  // simple inline styles similar to your Admin widget — keep consistent look
  const widgetStyle = {
    position: 'fixed',
    right: '20px',
    bottom: '80px',
    width: '360px',
    zIndex: 1050,
    transition: 'all 0.18s ease',
  };
  const cardStyle = { display: 'flex', flexDirection: 'column', height: '100%', maxHeight: '60vh', overflow: 'hidden' };
  const messageListStyle = { padding: '12px', overflowY: 'auto', flex: '1 1 auto', backgroundColor: '#fff8f5' };
  const inputContainerStyle = { borderTop: '1px solid rgba(0,0,0,0.06)', padding: '8px', backgroundColor: '#fff' };
  const toggleBtnStyle = {
    position: 'fixed',
    right: '20px',
    bottom: '20px',
    width: '56px',
    height: '56px',
    borderRadius: '50%',
    backgroundColor: '#0d6efd',
    color: '#fff',
    border: 'none',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    boxShadow: '0 6px 18px rgba(15, 23, 42, 0.12)',
    zIndex: 1060,
  };

  return (
    <>
      {isOpen ? (
        <div style={widgetStyle}>
          <Card style={cardStyle} className="shadow-lg">
            <Card.Header className="d-flex justify-content-between align-items-center" style={{ backgroundColor: '#f8f9fa' }}>
              <div>
                <strong>Customer Support</strong>
                <div style={{ fontSize: 12, color: '#6c757d' }}>Billing & account help</div>
              </div>

              <div className="d-flex align-items-center">
                <Button
                  variant="outline-danger"
                  size="sm"
                  className="me-2"
                  onClick={() => setShowClearConfirm(true)}
                >
                  <Trash size={16} /> Clear
                </Button>
                <CloseButton onClick={() => setIsOpen(false)} />
              </div>
            </Card.Header>

            <div style={messageListStyle} className="d-flex flex-column">
              {messages.map((m, idx) => (
                <div key={idx} className={`d-flex ${m.from === 'user' ? 'justify-content-end' : 'justify-content-start'}`} style={{ marginBottom: 8 }}>
                  <div
                    style={{
                      maxWidth: '75%',
                      padding: '8px 12px',
                      borderRadius: 12,
                      backgroundColor: m.from === 'user' ? '#0d6efd' : '#f1f3f5',
                      color: m.from === 'user' ? '#fff' : '#212529',
                      wordBreak: 'break-word',
                    }}
                  >
                    {m.text}
                  </div>
                </div>
              ))}

              {loading && (
                <div className="d-flex align-items-center" style={{ marginTop: 6 }}>
                  <Spinner animation="border" size="sm" className="me-2" />
                  <small>Thinking...</small>
                </div>
              )}

              <div ref={messagesEndRef} />
            </div>

            <div style={inputContainerStyle}>
              <Form onSubmit={handleSubmit} className="d-flex align-items-center">
                <Form.Control
                  type="text"
                  placeholder="Ask about your invoices, payments or plan..."
                  value={input}
                  onChange={(e) => setInput(e.target.value)}
                  disabled={loading}
                  className="me-2"
                  style={{ borderRadius: 6, flex: 1 }}
                />
                <Button variant="primary" type="submit" disabled={loading}>
                  <SendFill />
                </Button>
              </Form>
            </div>
          </Card>
        </div>
      ) : null}

      {/* Toggle */}
      <button style={toggleBtnStyle} onClick={() => setIsOpen(prev => !prev)} aria-label="Open chat">
        {isOpen ? <X size={28} /> : <ChatDots size={28} />}
      </button>

      {/* Clear confirmation modal */}
      <Modal show={showClearConfirm} onHide={() => setShowClearConfirm(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Clear chat history</Modal.Title>
        </Modal.Header>
        <Modal.Body>Are you sure you want to clear your chat history? This cannot be undone.</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowClearConfirm(false)}>
            Cancel
          </Button>
          <Button variant="danger" onClick={handleClearHistory} disabled={loading}>
            {loading ? <Spinner animation="border" size="sm" /> : 'Clear'}
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
}

export default CustomerChatbot;

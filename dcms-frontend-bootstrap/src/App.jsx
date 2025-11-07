import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { Alert } from 'react-bootstrap';

// --- Page Imports ---
import LoginPage from './components/LoginPage';
import SignupPage from './components/SignupPage';
import ProtectedRoute from './components/ProtectedRoute';

// --- Admin Imports ---
import AdminLayout from './components/AdminLayout';
import AdminDashboard from './components/admin/AdminDashboard';
import CustomersPage from './components/admin/CustomersPage';
import RulesPage from './components/admin/RulesPage';
import DunningPage from './components/admin/DunningPage';
import CuringPage from './components/admin/CuringPage';
import PaymentsPage from './components/admin/PaymentsPage';
import SettingsPage from './components/admin/SettingsPage';
function InsightsPage() { return <h2>AI Insights</h2>; }

// --- CUSTOMER IMPORTS ---
import CustomerLayout from './components/CustomerLayout';
import CustomerDashboard from './components/CustomerDashboard';
import BrowsePlans from './components/BrowsePlans';
import MySubscriptions from './components/MySubscriptions';
// --- NEW IMPORTS ---
import PaymentHistory from './components/PaymentHistory';
import Notifications from './components/Notifications';
import AiAssistant from './components/AiAssistant';
import Profile from './components/Profile';
// --- END NEW IMPORTS ---

// --- Placeholder Support Page ---
function SupportDashboard() {
  return <Alert variant="warning">Welcome, Support Agent!</Alert>;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* --- Public Auth Routes --- */}
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        
        {/* --- Protected Customer Route (UPDATED) --- */}
        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<CustomerLayout />}>
            <Route index element={<CustomerDashboard />} />
            <Route path="plans" element={<BrowsePlans />} />
            <Route path="subscriptions" element={<MySubscriptions />} />
            {/* --- ADD THESE ROUTES --- */}
            <Route path="payment-history" element={<PaymentHistory />} />
            <Route path="notifications" element={<Notifications />} />
            <Route path="ai-assistant" element={<AiAssistant />} />
            <Route path="profile" element={<Profile />} />
            {/* --- END OF ADDITION --- */}
          </Route>
        </Route>
        
        {/* --- Protected Support Route --- */}
        <Route element={<ProtectedRoute />}>
          <Route path="/support/dashboard" element={<SupportDashboard />} />
        </Route>

        {/* --- Protected Admin Routes --- */}
        <Route path="/admin" element={<ProtectedRoute />}>
          <Route element={<AdminLayout />}> 
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="customers" element={<CustomersPage />} />
            <Route path="rules" element={<RulesPage />} />
            <Route path="dunning" element={<DunningPage />} />
            <Route path="curing" element={<CuringPage />} />
            <Route path="payments" element={<PaymentsPage />} />
            <Route path="insights" element={<InsightsPage />} />
            <Route path="settings" element={<SettingsPage />} />
            <Route index element={<AdminDashboard />} /> 
          </Route>
        </Route>
        
      </Routes>
    </BrowserRouter>
  );
}

export default App;
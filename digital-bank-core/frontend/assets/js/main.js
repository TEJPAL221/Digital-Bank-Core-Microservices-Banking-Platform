// assets/js/main.js
// Immediately invoked function to avoid polluting global scope
(() => {
  // If API Gateway is same domain/port via Nginx, keep empty. Otherwise put full base like 'http://localhost:8080'
  const API_BASE = ""; 

  // Pages that require login
  const PROTECTED_PAGES = new Set([
    'accounts.html',
    'transactions.html',
    'loans.html',
    'notifications.html',
    'payments.html',
    // If you serve the dashboard at index.html only for authenticated users, add it here:
    // 'index.html'
  ]);

  // Helper: get current page filename
  function currentPage() {
    const p = window.location.pathname.split('/').pop();
    return p || 'index.html';
  }

  // Check if user is logged in (JWT token in localStorage)
  function isLoggedIn() {
    return !!localStorage.getItem('jwtToken');
  }

  // Toggle public/guest UI (e.g., auth-cta bar)
  function setGuestModeUI(enabled) {
    const authCta = document.getElementById('auth-cta');
    if (authCta) authCta.style.display = enabled ? 'block' : 'none';

    // Optionally hide the Logout nav item when guest
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn && logoutBtn.parentElement) {
      logoutBtn.parentElement.style.display = enabled ? 'none' : '';
    }
  }

  // Logout button behavior
  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', (e) => {
      e.preventDefault();
      localStorage.removeItem('jwtToken');
      window.location.href = 'login.html';
    });
  }

  // Auth gate: only redirect if user is on a protected page and not logged in
  const page = currentPage();
  const authed = isLoggedIn();

  if (!authed && PROTECTED_PAGES.has(page)) {
    // Not logged in and trying to open a protected page → go to login
    window.location.href = 'login.html';
    return; // Stop executing further on this page
  } else {
    // Public page (index.html, login.html, register.html, etc.) or user is logged in
    setGuestModeUI(!authed);
  }

  // Fetch helper with JWT Auth header
  async function authFetch(url, options = {}) {
    const token = localStorage.getItem('jwtToken');
    options.headers = options.headers || {};
    if (token) {
      options.headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(API_BASE + url, options);

    if (!response.ok) {
      if (response.status === 401) {
        // Unauthorized → clear token and go to login
        alert('Session expired or unauthorized. Please login again.');
        localStorage.removeItem('jwtToken');
        window.location.href = 'login.html';
        return Promise.reject(new Error('Unauthorized'));
      }
      const text = await response.text().catch(() => '');
      throw new Error(`HTTP error ${response.status}: ${text || 'Request failed'}`);
    }
    // If content-type is JSON, parse; otherwise return raw text
    const ct = response.headers.get('content-type') || '';
    if (ct.includes('application/json')) return response.json();
    return response.text();
  }

  // Load Dashboard data (accounts, transactions, loans, notifications) only if logged in
  async function loadDashboardData() {
    if (!authed) {
      // Guest mode: show a simple hint; avoid hitting protected APIs
      const hint = 'Login to see your personalized data.';
      const byId = (id) => document.getElementById(id);
      const accountEl = byId('account-info');
      const txEl = byId('transactions-info');
      const loanEl = byId('loan-info');
      const notifEl = byId('notifications-info');
      if (accountEl) accountEl.textContent = hint;
      if (txEl) txEl.textContent = hint;
      if (loanEl)  loanEl.textContent = hint;
      if (notifEl) notifEl.textContent = hint;
      return;
    }

    try {
      // Account Summary
      const accounts = await authFetch('/api/accounts'); // adapt endpoint as needed
      const accountInfoEl = document.getElementById('account-info');
      if (accountInfoEl) {
        const count = Array.isArray(accounts) ? accounts.length : 0;
        accountInfoEl.innerText = `You have ${count} accounts.`;
      }

      // Recent Transactions (last 5)
      const transactions = await authFetch('/api/transactions'); // adapt endpoint for user
      const txEl = document.getElementById('transactions-info');
      if (txEl && Array.isArray(transactions)) {
        const recentTx = transactions.slice(0, 5).map(tx =>
          `<li>${tx.type} of $${tx.amount} on ${new Date(tx.timestamp).toLocaleDateString()}</li>`
        ).join('');
        txEl.innerHTML = `<ul>${recentTx}</ul>`;
      }

      // Loan Status
      const loans = await authFetch('/api/loans/account/1'); // replace 1 with actual accountId
      const loanEl = document.getElementById('loan-info');
      if (loanEl) {
        const loanStatus = (Array.isArray(loans) && loans.length)
          ? loans.map(loan =>
              `<li>Loan #${loan.id}: Outstanding $${Number(loan.outstanding).toFixed(2)}</li>`
            ).join('')
          : 'No loans';
        loanEl.innerHTML = `<ul>${loanStatus}</ul>`;
      }

      // Notifications
      const notifications = await authFetch('/api/notifications/user/1'); // replace 1 with actual userId
      const notifEl = document.getElementById('notifications-info');
      if (notifEl) {
        const notes = (Array.isArray(notifications) && notifications.length)
          ? notifications.map(note =>
              `<li>${note.message} - ${new Date(note.timestamp).toLocaleString()}</li>`
            ).join('')
          : 'No notifications';
        notifEl.innerHTML = `<ul>${notes}</ul>`;
      }
    } catch (error) {
      console.error('Error loading dashboard data:', error);
      const accountInfoEl = document.getElementById('account-info');
      if (accountInfoEl) accountInfoEl.innerText = 'Failed to load account info.';
    }
  }

  // Run on dashboard page only (as you had)
  if (document.getElementById('account-info')) {
    loadDashboardData();
  }
})();

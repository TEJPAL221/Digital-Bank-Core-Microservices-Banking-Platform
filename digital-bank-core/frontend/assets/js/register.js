// assets/js/register.js

document.getElementById('registerForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const fullName = document.getElementById('fullName').value.trim();
  const email = document.getElementById('regEmail').value.trim();
  const password = document.getElementById('regPassword').value;

  try {
    // Adjust URL to your API gateway route if different
    const res = await fetch('http://localhost:8080/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ fullName, email, password })
    });

    if (!res.ok) {
      const errText = await res.text();
      alert(`Registration failed: ${errText || res.status}`);
      return;
    }

    alert('Registration successful. Please log in.');
    window.location.href = 'login.html';
  } catch (err) {
    console.error(err);
    alert('Network error during registration');
  }
});

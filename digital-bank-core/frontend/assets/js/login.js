document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('login-form');
    const errorMsg = document.getElementById('login-error');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorMsg.textContent = '';

        const username = form.username.value.trim();
        const password = form.password.value.trim();

        if (!username || !password) {
            errorMsg.textContent = 'Please enter both username and password.';
            return;
        }

        try {
            const response = await fetch('/api/auth/login', {  // Adjust path to your login API
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (!response.ok) {
                errorMsg.textContent = 'Invalid username or password.';
                return;
            }

            const data = await response.json();
            if (data.token) {
                localStorage.setItem('jwtToken', data.token);
                window.location.href = 'index.html';
            } else {
                errorMsg.textContent = 'Login failed: token missing.';
            }
        } catch (err) {
            errorMsg.textContent = 'Server error. Please try again later.';
            console.error('Login error:', err);
        }
    });
});

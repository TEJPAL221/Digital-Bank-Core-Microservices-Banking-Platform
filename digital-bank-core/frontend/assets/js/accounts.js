document.addEventListener('DOMContentLoaded', async () => {
    const accountsList = document.getElementById('accounts-list');
    const errorMsg = document.getElementById('accounts-error');

    try {
        const accounts = await authFetch('/api/accounts');
        if (accounts.length === 0) {
            accountsList.innerHTML = '<li>No accounts found.</li>';
            return;
        }

        accountsList.innerHTML = accounts.map(acc => `
            <li>
                <strong>Type:</strong> ${acc.type} <br />
                <strong>Balance:</strong> $${acc.balance.toFixed(2)}
            </li>
        `).join('');
    } catch (err) {
        console.error(err);
        errorMsg.textContent = 'Failed to load accounts.';
    }
});

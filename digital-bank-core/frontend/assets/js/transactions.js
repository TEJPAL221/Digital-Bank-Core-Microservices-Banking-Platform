document.addEventListener('DOMContentLoaded', async () => {
    const tableBody = document.querySelector('#transactions-table tbody');
    const errorMsg = document.getElementById('transactions-error');

    try {
        const transactions = await authFetch('/api/transactions');
        if (transactions.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="3">No transactions available.</td></tr>`;
            return;
        }

        tableBody.innerHTML = transactions.map(tx => `
            <tr>
                <td>${tx.type}</td>
                <td>${tx.amount.toFixed(2)}</td>
                <td>${new Date(tx.timestamp).toLocaleString()}</td>
            </tr>
        `).join('');
    } catch (err) {
        console.error(err);
        errorMsg.textContent = 'Failed to load transactions.';
    }
});

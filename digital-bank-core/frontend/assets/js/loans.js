document.addEventListener('DOMContentLoaded', async () => {
    const tableBody = document.querySelector('#loans-table tbody');
    const errorMsg = document.getElementById('loans-error');

    try {
        // Replace accountId=1 with actual logged-in user's account id
        const loans = await authFetch('/api/loans/account/1');
        if (loans.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6">No loans found.</td></tr>`;
            return;
        }

        tableBody.innerHTML = loans.map(loan => `
            <tr>
                <td>${loan.id}</td>
                <td>${loan.amount.toFixed(2)}</td>
                <td>${loan.outstanding.toFixed(2)}</td>
                <td>${loan.interestRate}</td>
                <td>${new Date(loan.startDate).toLocaleDateString()}</td>
                <td>${new Date(loan.dueDate).toLocaleDateString()}</td>
            </tr>
        `).join('');
    } catch (err) {
        console.error(err);
        errorMsg.textContent = 'Failed to load loans.';
    }
});

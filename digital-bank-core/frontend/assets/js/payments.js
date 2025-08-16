document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('payment-form');
    const message = document.getElementById('payment-msg');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        message.textContent = '';
        const accountId = parseInt(form.accountId.value.trim());
        const amount = parseFloat(form.amount.value.trim());

        if (isNaN(accountId) || isNaN(amount) || amount <= 0) {
            message.textContent = 'Please enter valid account ID and amount.';
            message.style.color = 'red';
            return;
        }

        try {
            const paymentData = {
                accountId: accountId,
                amount: amount
            };

            const response = await authFetch('/api/payments', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(paymentData),
            });

            message.textContent = `Payment submitted successfully! Payment ID: ${response.id}`;
            message.style.color = 'green';
            form.reset();
        } catch (err) {
            console.error(err);
            message.textContent = 'Failed to submit payment.';
            message.style.color = 'red';
        }
    });
});

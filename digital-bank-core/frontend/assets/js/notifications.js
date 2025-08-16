document.addEventListener('DOMContentLoaded', async () => {
    const notificationsList = document.getElementById('notifications-list');
    const errorMsg = document.getElementById('notifications-error');

    try {
        // Replace 1 with actual logged-in user id
        const notifications = await authFetch('/api/notifications/user/1');
        if (notifications.length === 0) {
            notificationsList.innerHTML = '<li>No notifications.</li>';
            return;
        }

        notificationsList.innerHTML = notifications.map(note => `
            <li ${note.read ? 'class="read"' : ''}>
                ${note.message} <br />
                <small>${new Date(note.timestamp).toLocaleString()}</small>
            </li>
        `).join('');
    } catch (err) {
        console.error(err);
        errorMsg.textContent = 'Failed to load notifications.';
    }
});

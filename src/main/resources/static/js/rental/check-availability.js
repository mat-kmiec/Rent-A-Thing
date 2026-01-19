document.addEventListener('DOMContentLoaded', function () {
    const dateFrom = document.getElementById('dateFrom') || document.getElementById('startDate');
    const dateTo = document.getElementById('dateTo') || document.getElementById('endDate');
    const itemIdInput = document.getElementById('itemId');
    const statusDiv = document.getElementById('availability-status');
    const statusText = document.getElementById('status-text');
    const statusIcon = document.getElementById('status-icon');
    const reserveBtn = document.getElementById('reserveBtn') || document.getElementById('submitBtn');

    if (!dateFrom || !dateTo || !itemIdInput) return;

    const itemId = itemIdInput.value;

    async function checkAvailability() {
        const start = dateFrom.value;
        const end = dateTo.value;

        if (start && end) {
            try {
                const response = await fetch(`/api/rentals/${itemId}/availability?startDate=${start}&endDate=${end}`);

                if (!response.ok) {
                    const errorData = await response.json();
                    updateUI(errorData.message, 'alert-warning', 'fa-triangle-exclamation', true);
                    return;
                }

                const isAvailable = await response.json();

                if (isAvailable) {
                    updateUI('Termin jest dostępny!', 'alert-success', 'fa-circle-check', false);
                } else {
                    updateUI('Termin zajęty w wybranym zakresie.', 'alert-danger', 'fa-circle-xmark', true);
                }
            } catch (error) {
                console.error('Błąd API:', error);
            }
        }
    }

    function updateUI(message, alertClass, iconClass, isDisabled) {
        if (!statusDiv) return;

        statusDiv.classList.remove('d-none', 'alert-success', 'alert-danger', 'alert-warning');
        statusDiv.classList.add(alertClass);
        statusText.textContent = message;
        statusIcon.className = 'fa-solid ' + iconClass + ' me-2';

        if (reserveBtn) {
            reserveBtn.classList.toggle('disabled', isDisabled); // Dla <a>
            reserveBtn.disabled = isDisabled; // Dla <button>
        }
    }

    dateFrom.addEventListener('change', checkAvailability);
    dateTo.addEventListener('change', checkAvailability);
    if(dateFrom.value && dateTo.value) checkAvailability();
});
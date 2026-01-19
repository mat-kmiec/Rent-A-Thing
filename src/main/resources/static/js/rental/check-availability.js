document.addEventListener('DOMContentLoaded', function () {
    const dateFrom = document.getElementById('dateFrom');
    const dateTo = document.getElementById('dateTo');
    const itemIdInput = document.getElementById('itemId');
    const statusDiv = document.getElementById('availability-status');
    const statusText = document.getElementById('status-text');
    const statusIcon = document.getElementById('status-icon');
    const reserveBtn = document.getElementById('reserveBtn');

    if (!dateFrom || !dateTo || !itemIdInput || !reserveBtn) return;

    const itemId = itemIdInput.value;

    async function checkAvailability() {
        const start = dateFrom.value;
        const end = dateTo.value;

        if (start && end) {
            try {
                const response = await fetch(`/api/rentals/${itemId}/availability?startDate=${start}&endDate=${end}`);

                if (!response.ok) {
                    const errorData = await response.json();
                    updateUI(errorData.message, 'alert-warning', 'fa-triangle-exclamation', false);
                    return;
                }

                const isAvailable = await response.json();

                if (isAvailable) {
                    const checkoutUrl = `/wypozyczenia/formularz?przedmiot=${itemId}&start=${start}&koniec=${end}`;
                    updateUI('Świetnie! Ten termin jest dostępny.', 'alert-success', 'fa-circle-check', true, checkoutUrl);
                } else {
                    updateUI('Przepraszamy, ten termin jest już zajęty.', 'alert-danger', 'fa-circle-xmark', false);
                }
            } catch (error) {
                console.error('Błąd API:', error);
            }
        }
    }

    function updateUI(message, alertClass, iconClass, isAvailable, url = "#") {
        statusDiv.classList.remove('d-none', 'alert-success', 'alert-danger', 'alert-warning');
        statusDiv.classList.add(alertClass);
        statusText.textContent = message;
        statusIcon.className = 'fa-solid ' + iconClass + ' me-2';
        if (isAvailable) {
            reserveBtn.classList.remove('disabled');
            reserveBtn.href = url;
        } else {
            reserveBtn.classList.add('disabled');
            reserveBtn.href = "#";
        }
    }

    dateFrom.addEventListener('change', checkAvailability);
    dateTo.addEventListener('change', checkAvailability);
});
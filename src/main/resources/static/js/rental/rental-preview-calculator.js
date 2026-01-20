document.addEventListener('DOMContentLoaded', function () {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const reserveBtn = document.getElementById('reserveBtn');
    const itemIdInput = document.getElementById('itemId');
    const priceElement = document.getElementById('priceDisplay');
    if (!startDateInput || !endDateInput || !priceElement || !itemIdInput) return;

    const itemId = itemIdInput.value;
    const rawPrice = parseFloat(priceElement.getAttribute('data-price'));

    function calculate() {
        const start = new Date(startDateInput.value);
        const end = new Date(endDateInput.value);

        let days = 0;
        if (startDateInput.value && endDateInput.value && end >= start) {
            const diffTime = Math.abs(end - start);
            days = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            if (days === 0) days = 1;
        }

        let rentalTotal = days * rawPrice;

        const totalSum = rentalTotal;
        const summaryBox = document.querySelector('.bg-light.p-3.rounded-3');
        if (summaryBox) {
            summaryBox.innerHTML = `
                <div class="d-flex justify-content-between mb-1">
                    <small class="text-muted">${rawPrice.toFixed(2)} zł x ${days} dni</small>
                    <small class="fw-bold">${rentalTotal.toFixed(2)} zł</small>
                </div>
                <hr class="my-2 border-secondary opacity-25">
                <div class="d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-dark">Razem</span>
                    <span class="fw-bold text-primary fs-5">${totalSum.toFixed(2)} zł</span>
                </div>
            `;
        }

        if (days > 0 && startDateInput.value && endDateInput.value) {
            const url = `/wypozyczenia/formularz?przedmiot=${itemId}&start=${startDateInput.value}&koniec=${endDateInput.value}`;
            reserveBtn.setAttribute('href', url);
        }
    }

    startDateInput.addEventListener('change', calculate);
    endDateInput.addEventListener('change', calculate);
});
document.addEventListener('DOMContentLoaded', function () {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const daysCountSpan = document.getElementById('daysCount');
    const rentalCostSpan = document.getElementById('rentalCost');
    const deliveryCostSpan = document.getElementById('deliveryCost');
    const totalCostSpan = document.getElementById('totalCost');
    const pricePerDay = parseFloat(document.getElementById('rawPricePerDay').value);
    const depositElement = document.getElementById('rawDepositPrice');
    const depositPrice = depositElement ? parseFloat(depositElement.value) : 0;
    const discountPercent = 0;
    function calculateTotal() {
        const start = new Date(startDateInput.value);
        const end = new Date(endDateInput.value);

        let days = 0;
        if (startDateInput.value && endDateInput.value && end >= start) {
            const diffTime = Math.abs(end - start);
            days = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            if (days === 0) days = 1;
        }
        daysCountSpan.textContent = days;
        let rentalTotal = pricePerDay * days;
        rentalCostSpan.textContent = rentalTotal.toFixed(2);
        let shippingCost = 0;
        const selectedDelivery = document.querySelector('input[name="deliveryMethod"]:checked');

        if (selectedDelivery) {
            shippingCost = parseFloat(selectedDelivery.getAttribute('data-price')) || 0;
        }
        deliveryCostSpan.textContent = shippingCost.toFixed(2);
        const total = rentalTotal + shippingCost + depositPrice;
        totalCostSpan.textContent = total.toFixed(2);
    }

    startDateInput.addEventListener('change', calculateTotal);
    endDateInput.addEventListener('change', calculateTotal);

    const deliveryRadios = document.querySelectorAll('input[name="deliveryMethod"]');
    deliveryRadios.forEach(radio => {
        radio.addEventListener('change', calculateTotal);
    });

    calculateTotal();
});
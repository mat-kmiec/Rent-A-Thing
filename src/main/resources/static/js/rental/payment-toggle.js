document.addEventListener('DOMContentLoaded', function() {
    const paymentRadios = document.querySelectorAll('input[name="payment"]');
    const submitBtn = document.getElementById('submitBtn');

    paymentRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            if (this.value === 'CASH') {
                submitBtn.innerText = 'Potwierdzam rezerwację';
            } else {
                submitBtn.innerText = 'Potwierdzam i Płacę';
            }
        });
    });
});
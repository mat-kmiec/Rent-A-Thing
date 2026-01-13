document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const toastEl = document.getElementById('liveToast');
    const toastBody = document.getElementById('toastMessage');
    const toastIcon = document.getElementById('toastIcon');

    if (!toastEl) return;

    const toast = new bootstrap.Toast(toastEl, { delay: 5000 });

    const types = {
        'success': { class: 'bg-success', icon: 'fa-check-circle', msg: 'Operacja zakończona sukcesem!' },
        'error':   { class: 'bg-danger',  icon: 'fa-exclamation-circle', msg: 'Wystąpił błąd. Spróbuj ponownie.' },
        'warning': { class: 'bg-warning', icon: 'fa-exclamation-triangle', msg: 'Uwaga! Sprawdź dane.' },
        'logout':  { class: 'bg-success', icon: 'fa-info-circle', msg: 'Zostałeś wylogowany.' },
        'loggedIn':  { class: 'bg-success', icon: 'fa-info-circle', msg: 'Jesteś już zalogowany!.' }
    };

    for (const [key, config] of Object.entries(types)) {
        if (urlParams.has(key)) {
            toastEl.classList.add(config.class);
            toastIcon.classList.add('fa-solid', config.icon);
            toastBody.textContent = config.msg;
            toast.show();
            break;
        }
    }
});
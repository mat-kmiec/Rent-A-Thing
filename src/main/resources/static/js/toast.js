document.addEventListener('DOMContentLoaded', function () {
    const toastEl = document.getElementById('liveToast');
    const toastBody = document.getElementById('toastMessage');
    const toastIcon = document.getElementById('toastIcon');
    const serverType = document.getElementById('serverToastType')?.value;
    const serverMsg = document.getElementById('serverToastMsg')?.value;

    if (!toastEl) return;

    const toast = new bootstrap.Toast(toastEl, {
        delay: 5000,
        autohide: true
    });

    const configs = {
        'success': { class: 'bg-success', icon: 'fa-check-circle', defaultMsg: 'Operacja zakończona sukcesem!' },
        'error':   { class: 'bg-danger',  icon: 'fa-circle-xmark',  defaultMsg: 'Wystąpił błąd. Spróbuj ponownie.' },
        'warning': { class: 'bg-warning', icon: 'fa-triangle-exclamation', defaultMsg: 'Uwaga! Sprawdź dane.' },
        'info':    { class: 'bg-info',    icon: 'fa-circle-info',   defaultMsg: 'Informacja systemowa.' }
    };

    function showToast(type, message) {
        const config = configs[type] || configs['info'];
        toastEl.classList.remove('bg-success', 'bg-danger', 'bg-warning', 'bg-info');
        toastIcon.className = 'me-2 fa-solid';
        toastEl.classList.add(config.class);
        toastIcon.classList.add(config.icon);
        toastBody.textContent = message || config.defaultMsg;

        toast.show();
    }

    function cleanUrlFromToasts() {
        const url = new URL(window.location.href);
        const params = url.searchParams;
        const toastParams = ['success', 'error', 'warning', 'info'];

        let changed = false;
        toastParams.forEach(p => {
            if (params.has(p)) {
                params.delete(p);
                changed = true;
            }
        });

        if (changed) {
            const newSearch = params.toString();
            const newUrl = url.pathname + (newSearch ? '?' + newSearch : '');
            window.history.replaceState({}, document.title, newUrl);
        }
    }

    if (serverType && serverMsg) {
        showToast(serverType, serverMsg);
    }
    else {
        const urlParams = new URLSearchParams(window.location.search);
        for (const type of Object.keys(configs)) {
            if (urlParams.has(type)) {
                showToast(type);
                break;
            }
        }
    }

    setTimeout(cleanUrlFromToasts, 1000);
});
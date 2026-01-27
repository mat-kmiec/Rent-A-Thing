/**
 * Configuration for API endpoints used in the administrative rental creation module.
 */
const endpoints = {
    users: '/admin/rentals/api/users/search',
    items: '/admin/rentals/api/items/search',
    availability: '/api/rentals'
};

/**
 * Current state of the selected rental item.
 */
let selectedItem = {
    price: 0,
    deposit: 0,
    shipping: 0,
    canShip: false
};

/**
 * Calculates and updates the rental price summary in the UI based on selected dates and delivery method.
 */
function updatePriceSummary() {
    const startVal = document.getElementById('startDate').value;
    const endVal = document.getElementById('endDate').value;
    const itemId = document.getElementById('itemId').value;
    const deliveryMethod = document.getElementById('deliveryMethod').value;

    const summaryDiv = document.getElementById('price-summary');
    const placeholderDiv = document.getElementById('price-placeholder');

    const hideSummary = () => {
        summaryDiv.classList.add('d-none');
        placeholderDiv.classList.remove('d-none');
    };

    if (!startVal || !endVal || !itemId || selectedItem.price <= 0) {
        hideSummary();
        return;
    }

    const start = new Date(startVal);
    const end = new Date(endVal);

    const diffTime = end - start;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

    if (diffDays <= 0 || isNaN(diffDays)) {
        hideSummary();
        return;
    }

    const rentalCost = diffDays * selectedItem.price;
    const shippingCost = (deliveryMethod === 'DELIVERY' && selectedItem.canShip) ? selectedItem.shipping : 0;
    const total = rentalCost + selectedItem.deposit + shippingCost;

    document.getElementById('summary-daily-rate').innerText = selectedItem.price.toFixed(2);
    document.getElementById('summary-days').innerText = diffDays;
    document.getElementById('summary-rental-price').innerText = rentalCost.toFixed(2);
    document.getElementById('summary-deposit').innerText = selectedItem.deposit.toFixed(2);
    document.getElementById('summary-total').innerText = total.toFixed(2);

    summaryDiv.classList.remove('d-none');
    placeholderDiv.classList.add('d-none');

    const deliverySelect = document.getElementById('deliveryMethod');
    const deliveryOption = deliverySelect.querySelector('option[value="DELIVERY"]');

    if (!selectedItem.canShip) {
        if (deliverySelect.value === 'DELIVERY') deliverySelect.value = 'PICKUP';
        deliveryOption.disabled = true;
        deliveryOption.innerText = "Wysyłka (Niedostępna dla tego przedmiotu)";
    } else {
        deliveryOption.disabled = false;
        deliveryOption.innerText = `Wysyłka (+${selectedItem.shipping.toFixed(2)} zł)`;
    }
}

/**
 * Asynchronously checks item availability via API for the selected date range.
 * Updates the UI with availability status and toggles the submit button.
 */
async function checkAdminAvailability() {
    const start = document.getElementById('startDate').value;
    const end = document.getElementById('endDate').value;
    const itemId = document.getElementById('itemId').value;

    const statusDiv = document.getElementById('availability-status');
    const statusText = document.getElementById('status-text');
    const statusIcon = document.getElementById('status-icon');
    const submitBtn = document.getElementById('submitBtn');

    if (!itemId || !start || !end) return;

    try {
        const response = await fetch(`${endpoints.availability}/${itemId}/availability?startDate=${start}&endDate=${end}`);

        if (!response.ok) {
            const errorData = await response.json();
            updateUI(errorData.message || 'Błąd dat', 'alert-warning', 'fa-triangle-exclamation', true);
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

    function updateUI(message, alertClass, iconClass, isDisabled) {
        statusDiv.classList.remove('d-none', 'alert-success', 'alert-danger', 'alert-warning');
        statusDiv.classList.add(alertClass);
        statusText.textContent = message;
        statusIcon.className = 'fa-solid ' + iconClass + ' me-2';
        submitBtn.disabled = isDisabled;
        submitBtn.classList.toggle('disabled', isDisabled);
    }
}

/**
 * Returns a function that delays execution of the provided function until after a timeout.
 * @param {Function} func - The function to debounce.
 * @param {number} timeout - Delay in milliseconds.
 */
function debounce(func, timeout = 300) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => { func.apply(this, args); }, timeout);
    };
}

/**
 * Sets the selected item, updates its state, and refreshes the UI.
 * Exposed to global scope for use in HTML onclick handlers.
 */
window.selectItem = (id, title, sku, img, price, deposit, shipping, canShip) => {
    document.getElementById('itemId').value = id;
    document.getElementById('selectedItemTitle').innerText = title;
    document.getElementById('selectedItemSku').innerText = 'SKU: ' + sku;
    document.getElementById('selectedItemImg').src = img;

    selectedItem = {
        price: parseFloat(price),
        deposit: parseFloat(deposit),
        shipping: parseFloat(shipping),
        canShip: (canShip === true || canShip === 'true')
    };

    document.getElementById('itemSearchContainer').classList.add('d-none');
    document.getElementById('selectedItemCard').classList.remove('d-none');
    document.getElementById('itemResults').classList.remove('show');

    updatePriceSummary();
    checkAdminAvailability();
};

const itemIn = document.getElementById('itemSearchInput');
const itemResults = document.getElementById('itemResults');

itemIn.addEventListener('input', debounce(async (e) => {
    const query = e.target.value;
    if (query.length < 2) { itemResults.classList.remove('show'); return; }

    const res = await fetch(`${endpoints.items}?query=${encodeURIComponent(query)}`);
    const items = await res.json();

    itemResults.innerHTML = '';
    items.forEach(i => {
        const li = document.createElement('li');
        li.innerHTML = `
                <button type="button" class="dropdown-item py-2 d-flex align-items-center"
                    onclick="selectItem(${i.id}, '${i.title.replace(/'/g, "\\'")}', '${i.sku}', '${i.imageUrl}', ${i.pricePerDay}, ${i.depositPrice}, ${i.shippingPrice}, ${i.canBeShipped})">
                    <img src="${i.imageUrl}" class="rounded me-2" style="width: 40px; height: 40px; object-fit: cover;">
                    <div>
                        <div class="fw-bold text-dark small">${i.title}</div>
                        <div class="text-muted" style="font-size: 0.7rem;">SKU: ${i.sku} | Doba: ${i.pricePerDay} zł</div>
                    </div>
                </button>`;
        itemResults.appendChild(li);
    });
    itemResults.classList.add('show');
}));

const userIn = document.getElementById('userSearchInput');
const userResults = document.getElementById('userResults');

userIn.addEventListener('input', debounce(async (e) => {
    const query = e.target.value;
    if (query.length < 2) { userResults.classList.remove('show'); return; }

    const res = await fetch(`${endpoints.users}?query=${encodeURIComponent(query)}`);
    const users = await res.json();

    userResults.innerHTML = users.map(u => `
            <li><button type="button" class="dropdown-item py-2" onclick="selectUser(${u.id}, '${u.label.replace(/'/g, "\\'")}', '${u.email}')">
                <i class="fa-solid fa-user me-2 text-muted"></i>${u.label}
            </button></li>`).join('');
    userResults.classList.add('show');
}));

/**
 * Selects a user and updates the UI.
 * Exposed to global scope for use in HTML onclick handlers.
 */
window.selectUser = (id, label, email) => {
    document.getElementById('userIdInput').value = id;
    document.getElementById('selectedUserLabel').innerText = label;
    document.getElementById('selectedUserEmail').innerText = email;
    document.getElementById('userSearchContainer').classList.add('d-none');
    document.getElementById('selectedUserCard').classList.remove('d-none');
    userResults.classList.remove('show');
};

/**
 * Resets the item selection and clears related UI elements.
 */
window.resetItem = () => {
    document.getElementById('itemId').value = '';
    selectedItem = { price: 0, deposit: 0, shipping: 0, canShip: false };
    itemIn.value = '';
    document.getElementById('itemSearchContainer').classList.remove('d-none');
    document.getElementById('selectedItemCard').classList.add('d-none');
    document.getElementById('availability-status').classList.add('d-none');
    updatePriceSummary();
};

/**
 * Resets the user selection and clears related UI elements.
 */
window.resetUser = () => {
    document.getElementById('userIdInput').value = '';
    userIn.value = '';
    document.getElementById('userSearchContainer').classList.remove('d-none');
    document.getElementById('selectedUserCard').classList.add('d-none');
};

document.addEventListener('DOMContentLoaded', () => {
    const inputs = ['startDate', 'endDate', 'deliveryMethod'];
    inputs.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.addEventListener('change', () => {
            updatePriceSummary();
            if (id !== 'deliveryMethod') checkAdminAvailability();
        });
    });

    document.addEventListener('click', (e) => {
        if (!itemIn.contains(e.target)) itemResults.classList.remove('show');
        if (!userIn.contains(e.target)) userResults.classList.remove('show');
    });
});
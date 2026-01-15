
    document.addEventListener('DOMContentLoaded', function() {
    const reviewForm = document.getElementById('reviewForm');
    const loadMoreBtn = document.getElementById('load-more-reviews');
    const itemId = loadMoreBtn.getAttribute('data-item-id');
    const reviewModal = new bootstrap.Modal(document.getElementById('addReviewModal'));

    reviewForm.addEventListener('submit', function(e) {
    e.preventDefault();

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const data = {
    authorName: document.getElementById('reviewAuthor').value,
    rating: parseInt(document.getElementById('reviewRating').value),
    content: document.getElementById('reviewContent').value
};

    fetch(`/api/reviews/${itemId}`, {
    method: 'POST',
    headers: {
    'Content-Type': 'application/json',
    [csrfHeader]: csrfToken
},
    body: JSON.stringify(data)
})
    .then(response => {
    if (response.ok) {
    reviewModal.hide();
    const currentPath = window.location.pathname;
    window.location.href = currentPath + '?success';
} else {
    const currentPath = window.location.pathname;
    window.location.href = currentPath + '?error';
}
})
    .catch(error => {
    console.error('Error:', error);
    const currentPath = window.location.pathname;
    window.location.href = currentPath + '?error';
});
});
});

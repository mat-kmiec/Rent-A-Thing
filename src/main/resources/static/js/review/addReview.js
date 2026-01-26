document.addEventListener('DOMContentLoaded', function() {
    const reviewForm = document.getElementById('reviewForm');
    const loadMoreBtn = document.getElementById('load-more-reviews');
    const modalElement = document.getElementById('addReviewModal');
    if (!reviewForm || !modalElement) return;

    const reviewModal = new bootstrap.Modal(modalElement);
    const itemId = loadMoreBtn ? loadMoreBtn.getAttribute('data-item-id') : document.getElementById('itemId').value;

    reviewForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const csrfTokenNode = document.querySelector('meta[name="_csrf"]');
        const csrfHeaderNode = document.querySelector('meta[name="_csrf_header"]');

        if (!csrfTokenNode || !csrfHeaderNode) {
            console.error("Brak tokenów CSRF w nagłówku strony!");
            return;
        }

        const data = {
            authorName: document.getElementById('reviewAuthor').value,
            rating: parseInt(document.getElementById('reviewRating').value),
            content: document.getElementById('reviewContent').value
        };

        fetch(`/api/reviews/${itemId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeaderNode.getAttribute('content')]: csrfTokenNode.getAttribute('content')
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) {
                    reviewModal.hide();
                    window.location.search = 'success';
                } else {
                    window.location.search = 'error';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                window.location.search = 'error';
            });
    });
});
document.addEventListener('DOMContentLoaded', function() {
    const reviewsList = document.getElementById('reviews-list');
    const loadMoreBtn = document.getElementById('load-more-reviews');
    const itemId = loadMoreBtn.getAttribute('data-item-id');

    window.fetchReviews = function(page) {
        fetch(`/api/reviews/${itemId}?page=${page}`)
            .then(response => response.json())
            .then(reviews => {
                if (reviews.length === 0) {
                    if (page === 0) reviewsList.innerHTML = '<p class="text-muted ps-3">Brak opinii.</p>';
                    loadMoreBtn.style.display = 'none';
                    return;
                }

                reviews.forEach(review => {
                    const initials = review.authorName.split(' ').map(n => n[0]).join('').toUpperCase();
                    const reviewHtml = `
                                <div class="border-bottom pb-4 mb-4">
                                    <div class="d-flex justify-content-between mb-2">
                                        <div class="d-flex align-items-center">
                                            <div class="review-avatar rounded-circle bg-primary bg-opacity-10 text-primary d-flex align-items-center justify-content-center me-3" style="width: 45px; height: 45px; font-weight: bold;">
                                                ${initials}
                                            </div>
                                            <div>
                                                <h6 class="fw-bold mb-0">${review.authorName}</h6>
                                                <div class="text-warning small">
                                                    ${'<i class="fa-solid fa-star"></i>'.repeat(review.rating)}${'<i class="fa-regular fa-star"></i>'.repeat(5 - review.rating)}
                                                </div>
                                            </div>
                                        </div>
                                        <small class="text-muted">${new Date(review.createdAt).toLocaleDateString()}</small>
                                    </div>
                                    <p class="text-muted mb-0 ps-5 ms-2">${review.content}</p>
                                </div>`;
                    reviewsList.insertAdjacentHTML('beforeend', reviewHtml);
                });

                loadMoreBtn.style.display = (reviews.length < 5) ? 'none' : 'inline-block';
            })
            .catch(error => console.error('Błąd pobierania:', error));
    }

    window.fetchReviews(0);

    loadMoreBtn.addEventListener('click', function() {
        let nextPage = parseInt(this.getAttribute('data-page')) + 1;
        this.setAttribute('data-page', nextPage);
        window.fetchReviews(nextPage);
    });
});
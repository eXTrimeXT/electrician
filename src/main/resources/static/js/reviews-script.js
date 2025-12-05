// Обработка рейтинга звездочками
function setupStarRating(starsContainer, hiddenInput) {
    const stars = starsContainer.querySelectorAll('.star');

    stars.forEach(star => {
        star.addEventListener('click', function() {
            const value = parseInt(this.getAttribute('data-value'));
            hiddenInput.value = value;

            stars.forEach((s, index) => {
                if (index < value) {
                    s.classList.add('active');
                } else {
                    s.classList.remove('active');
                }
            });
        });
    });
}

// Инициализация основного рейтинга
const mainRatingContainer = document.getElementById('ratingStars');
const mainRatingInput = document.getElementById('rating-value');
if (mainRatingContainer && mainRatingInput) {
    setupStarRating(mainRatingContainer, mainRatingInput);
}

// Функции для работы с модальными окнами
let currentEditReviewId = null;

function editReview(id, rating, comment) {
    currentEditReviewId = id;
    const modal = document.getElementById('editModal');
    const form = document.getElementById('editForm');
    const starsContainer = document.getElementById('editRatingStars');
    const ratingInput = document.getElementById('editRating');

    // Устанавливаем рейтинг
    ratingInput.value = rating;
    const stars = starsContainer.querySelectorAll('.star');
    stars.forEach(star => {
        const value = parseInt(star.getAttribute('data-value'));
        if (value <= rating) {
            star.classList.add('active');
        } else {
            star.classList.remove('active');
        }
    });

    // Настраиваем обработку звезд
    setupStarRating(starsContainer, ratingInput);

    // Устанавливаем текст
    document.getElementById('editComment').value = decodeHtml(comment);

    // Устанавливаем action формы
    form.action = `/reviews/${id}/edit`;

    // Показываем модальное окно
    modal.style.display = 'flex';
}

function replyToReview(id) {
    document.getElementById('replyReviewId').value = id;
    document.getElementById('replyForm').action = `/reviews/${id}/admin-response`;
    document.getElementById('replyModal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('editModal').style.display = 'none';
}

function closeReplyModal() {
    document.getElementById('replyModal').style.display = 'none';
}

function decodeHtml(html) {
    const txt = document.createElement("textarea");
    txt.innerHTML = html;
    return txt.value;
}

// Закрытие модальных окон при клике вне их
window.onclick = function(event) {
    const editModal = document.getElementById('editModal');
    const replyModal = document.getElementById('replyModal');

    if (event.target == editModal) closeModal();
    if (event.target == replyModal) closeReplyModal();
}

// Закрытие модальных окон по клавише ESC
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeModal();
        closeReplyModal();
    }
});
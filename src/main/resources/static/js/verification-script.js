// src/main/resources/static/js/verification-script.js
document.addEventListener('DOMContentLoaded', function() {
    const verificationCode = document.getElementById('verificationCode');
    const verifyBtn = document.getElementById('verifyBtn');
    const resendBtn = document.getElementById('resendBtn');
    const countdownElement = document.getElementById('countdown');
    const codeError = document.getElementById('codeError');

    let countdownInterval;
    let timeLeft = 1 * 60; // 1 минут в секундах

    // Запуск таймера
    startCountdown();

    // Валидация ввода кода (только цифры)
    verificationCode.addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g, '');
        hideError();

        // Автоподтверждение при вводе 6 цифр
        if (this.value.length === 6) {
            verifyCode();
        }
    });

    // Подтверждение кода
    document.getElementById('verificationForm').addEventListener('submit', function(e) {
        e.preventDefault();
        verifyCode();
    });

    // Повторная отправка кода
    resendBtn.addEventListener('click', function() {
        resendCode();
    });

    function startCountdown() {
        clearInterval(countdownInterval);
        timeLeft = 1 * 60;
        updateCountdown();

        countdownInterval = setInterval(function() {
            timeLeft--;
            updateCountdown();

            if (timeLeft <= 0) {
                clearInterval(countdownInterval);
                resendBtn.disabled = false;
                resendBtn.innerHTML = '<i class="fas fa-redo"></i> Отправить новый код';
            } else {
                resendBtn.disabled = true;
            }
        }, 1000);
    }

    function updateCountdown() {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        countdownElement.textContent =
            `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    }

    function verifyCode() {
        const code = verificationCode.value.trim();
        const userId = document.getElementById('userId').value;
        const email = document.getElementById('userEmail').value;

        if (code.length !== 6) {
            showError('Код должен содержать 6 цифр');
            return;
        }

        // Блокируем кнопку на время запроса
        verifyBtn.disabled = true;
        verifyBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Проверка...';

        $.ajax({
            url: '/register/verify',
            type: 'POST',
            data: {
                userId: userId,
                email: email,
                code: code
            },
            success: function(response) {
                if (response.success) {
                    // Успешное подтверждение
                    showSuccessMessage(response.message || 'Email успешно подтвержден!');

                    // Перенаправляем на страницу логина через 2 секунды
                    setTimeout(function() {
                        window.location.href = '/login?verified=true';
                    }, 2000);
                } else {
                    showError(response.error || 'Неверный код подтверждения');
                    verificationCode.select();
                    verificationCode.focus();
                    verifyBtn.disabled = false;
                    verifyBtn.innerHTML = '<i class="fas fa-check"></i> Подтвердить Email';
                }
            },
            error: function() {
                showError('Произошла ошибка при проверке кода');
                verifyBtn.disabled = false;
                verifyBtn.innerHTML = '<i class="fas fa-check"></i> Подтвердить Email';
            }
        });
    }

    function resendCode() {
        const userId = document.getElementById('userId').value;
        const email = document.getElementById('userEmail').value;

        resendBtn.disabled = true;
        resendBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Отправка...';

        $.ajax({
            url: '/register/resend',
            type: 'POST',
            data: {
                userId: userId,
                email: email
            },
            success: function(response) {
                if (response.success) {
                    // Сброс поля ввода и таймера
                    verificationCode.value = '';
                    verificationCode.focus();
                    hideError();
                    startCountdown();

                    // Показываем сообщение об успехе
                    showSuccessMessage(response.message || 'Новый код отправлен на вашу почту');
                } else {
                    showError(response.error || 'Не удалось отправить код');
                }
                resendBtn.disabled = true;
                resendBtn.innerHTML = '<i class="fas fa-redo"></i> Отправить снова';
            },
            error: function() {
                showError('Произошла ошибка при отправке кода');
                resendBtn.disabled = true;
                resendBtn.innerHTML = '<i class="fas fa-redo"></i> Отправить снова';
            }
        });
    }

    function showError(message) {
        codeError.querySelector('span').textContent = message;
        codeError.style.display = 'flex';
        verificationCode.classList.add('error');
    }

    function hideError() {
        codeError.style.display = 'none';
        verificationCode.classList.remove('error');
    }

    function showSuccessMessage(message) {
        // Удаляем предыдущие сообщения
        const existingMessages = document.querySelectorAll('.verification-container .success-message');
        existingMessages.forEach(msg => msg.remove());

        const successDiv = document.createElement('div');
        successDiv.className = 'success-message';
        successDiv.innerHTML = `
            <i class="fas fa-check-circle"></i>
            <span>${message}</span>
        `;

        // Вставляем сообщение в начало контейнера
        document.querySelector('.verification-container').insertBefore(
            successDiv,
            document.querySelector('.verification-content')
        );
    }
});
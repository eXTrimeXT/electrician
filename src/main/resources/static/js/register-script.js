// register-script.js - без изменений
document.addEventListener('DOMContentLoaded', function() {
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const registerForm = document.getElementById('registerForm');

    // Валидация паролей
    function validatePasswords() {
        if (password.value && confirmPassword.value) {
            if (password.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity('Пароли не совпадают');
                confirmPassword.classList.add('error');
            } else {
                confirmPassword.setCustomValidity('');
                confirmPassword.classList.remove('error');
                confirmPassword.classList.add('success');
            }
        }
    }

    password.addEventListener('input', validatePasswords);
    confirmPassword.addEventListener('input', validatePasswords);

    // Показать/скрыть пароль
    function setupPasswordToggle(inputId) {
        const input = document.getElementById(inputId);
        const toggle = document.createElement('span');

        toggle.innerHTML = '<i class="fas fa-eye"></i>';
        toggle.className = 'toggle-password';
        toggle.style.cursor = 'pointer';
        toggle.style.position = 'absolute';
        toggle.style.right = '15px';
        toggle.style.top = '40px';

        input.parentNode.style.position = 'relative';
        input.parentNode.insertBefore(toggle, input.nextSibling);

        toggle.addEventListener('click', function() {
            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);
            this.innerHTML = type === 'password'
                ? '<i class="fas fa-eye"></i>'
                : '<i class="fas fa-eye-slash"></i>';
        });
    }

    // Настройка переключателей для обоих полей пароля
    if (password) setupPasswordToggle('password');
    if (confirmPassword) setupPasswordToggle('confirmPassword');

    // Валидация формы перед отправкой
    registerForm.addEventListener('submit', function(e) {
        // Сбрасываем предыдущие ошибки
        clearErrors();

        // Дополнительная валидация
        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();

        // Проверка username
        if (username.length < 3) {
            showError('username', 'Логин должен содержать минимум 3 символа');
            e.preventDefault();
        }

        // Проверка email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            showError('email', 'Введите корректный email адрес');
            e.preventDefault();
        }

        // Проверка пароля
        if (password.value.length < 8) {
            showError('password', 'Пароль должен содержать минимум 8 символов');
            e.preventDefault();
        }

        // Проверка совпадения паролей
        if (password.value !== confirmPassword.value) {
            showError('confirmPassword', 'Пароли не совпадают');
            e.preventDefault();
        }
    });

    // Функции для отображения ошибок
    function showError(fieldName, message) {
        const field = document.getElementById(fieldName);
        const errorDiv = document.createElement('div');

        errorDiv.className = 'field-error';
        errorDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;

        field.classList.add('error');
        field.parentNode.appendChild(errorDiv);
    }

    function clearErrors() {
        const errors = document.querySelectorAll('.field-error');
        errors.forEach(error => error.remove());

        const errorFields = document.querySelectorAll('.error');
        errorFields.forEach(field => field.classList.remove('error'));
    }

    // Индикатор силы пароля
    if (password) {
        password.addEventListener('input', function() {
            const strength = checkPasswordStrength(this.value);
            updatePasswordStrengthIndicator(strength);
        });
    }

    function checkPasswordStrength(password) {
        let score = 0;

        if (password.length >= 8) score++;
        if (/[a-z]/.test(password)) score++;
        if (/[A-Z]/.test(password)) score++;
        if (/[0-9]/.test(password)) score++;
        if (/[^A-Za-z0-9]/.test(password)) score++;

        if (score <= 2) return 'weak';
        if (score <= 4) return 'medium';
        return 'strong';
    }

    function updatePasswordStrengthIndicator(strength) {
        let indicator = document.getElementById('passwordStrength');

        if (!indicator) {
            indicator = document.createElement('div');
            indicator.id = 'passwordStrength';
            indicator.className = 'password-strength';
            password.parentNode.appendChild(indicator);
        }

        indicator.className = 'password-strength';

        switch(strength) {
            case 'weak':
                indicator.classList.add('strength-weak');
                break;
            case 'medium':
                indicator.classList.add('strength-medium');
                break;
            case 'strong':
                indicator.classList.add('strength-strong');
                break;
        }
    }
});
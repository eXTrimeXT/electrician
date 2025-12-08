// src/main/resources/static/js/password-reset-script.js

document.addEventListener('DOMContentLoaded', function() {
    const newPassword = document.getElementById('newPassword');
    const confirmPassword = document.getElementById('confirmPassword');
    const submitBtn = document.getElementById('submitBtn');

    if (!newPassword || !confirmPassword) return;

    const reqLength = document.getElementById('reqLength');
    const reqLetter = document.getElementById('reqLetter');
    const reqNumber = document.getElementById('reqNumber');
    const passwordMatch = document.getElementById('passwordMatch');
    const passwordStrength = document.getElementById('passwordStrength');

    // Проверка всех требований
    function validateAll() {
        const password = newPassword.value;
        const confirm = confirmPassword.value;

        const isValid =
            validatePassword(password) &&
            validatePasswordMatch(password, confirm);

        submitBtn.disabled = !isValid;
        return isValid;
    }

    // Валидация пароля
    function validatePassword(password) {
        const hasLength = password.length >= 8;
        const hasLetter = /[a-zA-Z]/.test(password);
        const hasNumber = /\d/.test(password);

        // Обновляем индикаторы
        updateRequirement(reqLength, hasLength);
        updateRequirement(reqLetter, hasLetter);
        updateRequirement(reqNumber, hasNumber);

        // Обновляем индикатор силы
        updatePasswordStrength(password, hasLength, hasLetter, hasNumber);

        return hasLength && hasLetter && hasNumber;
    }

    // Обновление отображения требований
    function updateRequirement(element, isValid) {
        const icon = element.querySelector('i');
        if (isValid) {
            icon.className = 'fas fa-check';
            icon.style.color = '#27ae60';
            element.classList.remove('invalid');
        } else {
            icon.className = 'fas fa-times';
            icon.style.color = '#e74c3c';
            element.classList.add('invalid');
        }
    }

    // Обновление индикатора силы пароля
    function updatePasswordStrength(password, hasLength, hasLetter, hasNumber) {
        if (!passwordStrength) return;

        if (password.length === 0) {
            passwordStrength.className = 'password-strength';
            passwordStrength.style.width = '0';
            return;
        }

        let score = 0;
        if (hasLength) score++;
        if (hasLetter) score++;
        if (hasNumber) score++;
        if (/[^A-Za-z0-9]/.test(password)) score++;

        passwordStrength.className = 'password-strength';

        if (score <= 1) {
            passwordStrength.classList.add('strength-weak');
            passwordStrength.style.width = '25%';
        } else if (score <= 3) {
            passwordStrength.classList.add('strength-medium');
            passwordStrength.style.width = '60%';
        } else {
            passwordStrength.classList.add('strength-strong');
            passwordStrength.style.width = '100%';
        }
    }

    // Проверка совпадения паролей
    function validatePasswordMatch(password, confirm) {
        if (!passwordMatch) return false;

        if (confirm.length === 0) {
            passwordMatch.textContent = '';
            passwordMatch.className = 'password-match';
            return false;
        }

        if (password === confirm) {
            passwordMatch.textContent = 'Пароли совпадают ✓';
            passwordMatch.className = 'password-match valid';
            return true;
        } else {
            passwordMatch.textContent = 'Пароли не совпадают ✗';
            passwordMatch.className = 'password-match invalid';
            return false;
        }
    }

    // События ввода
    newPassword.addEventListener('input', function() {
        validatePassword(this.value);
        validateAll();
    });

    confirmPassword.addEventListener('input', function() {
        validatePasswordMatch(newPassword.value, this.value);
        validateAll();
    });

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

    // Настройка переключателей
    setupPasswordToggle('newPassword');
    setupPasswordToggle('confirmPassword');

    // Предотвращение отправки невалидной формы
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validateAll()) {
                e.preventDefault();
                alert('Пожалуйста, исправьте ошибки в форме перед отправкой.');
            }
        });
    }

    // Инициализация валидации
    validateAll();
});
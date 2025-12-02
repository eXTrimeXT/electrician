// Валидация паролей в реальном времени
document.addEventListener('DOMContentLoaded', function() {
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');

    function validatePasswords() {
        if (password.value && confirmPassword.value) {
            if (password.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity('Пароли не совпадают');
            } else {
                confirmPassword.setCustomValidity('');
            }
        }
    }

    password.addEventListener('input', validatePasswords);
    confirmPassword.addEventListener('input', validatePasswords);

    // Показать/скрыть пароль
    const togglePassword = document.createElement('span');
    togglePassword.innerHTML = '<i class="fas fa-eye"></i>';
    togglePassword.className = 'toggle-password';
    togglePassword.style.cursor = 'pointer';
    togglePassword.style.marginLeft = '10px';

    password.parentNode.insertBefore(togglePassword, password.nextSibling);

    togglePassword.addEventListener('click', function() {
        const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
        password.setAttribute('type', type);
        this.innerHTML = type === 'password' ? '<i class="fas fa-eye"></i>' : '<i class="fas fa-eye-slash"></i>';
    });
});
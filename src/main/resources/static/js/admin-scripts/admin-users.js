let currentPage = 1;
let totalPages = 1;
let searchQuery = '';
let roleFilter = '';
let statusFilter = '';

// Инициализация управления пользователями
function initUsersManagement() {
    setupUsersEventListeners();
    loadUsers();
}

// Настройка обработчиков событий
function setupUsersEventListeners() {
    // Поиск пользователей
    document.getElementById('search-users').addEventListener('click', function() {
        searchQuery = document.getElementById('user-search').value.trim();
        currentPage = 1;
        loadUsers();
    });

    // Поиск при нажатии Enter
    document.getElementById('user-search').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchQuery = this.value.trim();
            currentPage = 1;
            loadUsers();
        }
    });

    // Фильтры
    document.getElementById('role-filter').addEventListener('change', function() {
        roleFilter = this.value;
        currentPage = 1;
        loadUsers();
    });

    document.getElementById('status-filter').addEventListener('change', function() {
        statusFilter = this.value;
        currentPage = 1;
        loadUsers();
    });

    // Сброс фильтров
    document.getElementById('reset-filters').addEventListener('click', function() {
        document.getElementById('user-search').value = '';
        document.getElementById('role-filter').value = '';
        document.getElementById('status-filter').value = '';
        searchQuery = '';
        roleFilter = '';
        statusFilter = '';
        currentPage = 1;
        loadUsers();
    });
}

// Загрузка пользователей
async function loadUsers() {
    try {
        const params = new URLSearchParams({
            page: currentPage,
            search: searchQuery,
            role: roleFilter,
            status: statusFilter
        });

        const response = await axios.get(`/api/admin/users?${params}`);
        renderUsersTable(response.data.users);
        renderPagination(response.data.pagination);
    } catch (error) {
        console.error('Ошибка при загрузке пользователей:', error);
        showAlert('Ошибка при загрузке пользователей', 'error');
    }
}

// Отображение пользователей в таблице
function renderUsersTable(users) {
    const tbody = document.getElementById('users-table-body');
    tbody.innerHTML = '';

    if (users.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td colspan="9" style="text-align: center; padding: 2rem;">
                <i class="fas fa-users fa-2x" style="color: #ccc; margin-bottom: 1rem;"></i>
                <p>Пользователи не найдены</p>
            </td>
        `;
        tbody.appendChild(row);
        return;
    }

    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td><strong>${escapeHtml(user.username || '')}</strong></td>
            <td>${escapeHtml(user.email || '-')}</td>
            <td>${escapeHtml(user.role || 'USER')}</td>
            <td>
                <span class="status-badge ${user.active ? 'status-active' : 'status-inactive'}">
                    ${user.active ? 'Активен' : 'Заблокирован'}
                </span>
            </td>
            <td>
                <span class="status-badge ${user.email_verified ? 'status-verified' : 'status-not-verified'}">
                    ${user.email_verified ? 'Да' : 'Нет'}
                </span>
            </td>
            <td>${formatDateTime(user.created_at)}</td>
            <td>${formatDateTime(user.updated_at)}</td>
            <td>
                <div class="user-actions">
                    ${user.active ?
                        `<button class="user-action-btn block" onclick="toggleUserStatus(${user.id}, false)">
                            <i class="fas fa-ban"></i> Заблокировать
                        </button>` :
                        `<button class="user-action-btn unblock" onclick="toggleUserStatus(${user.id}, true)">
                            <i class="fas fa-check"></i> Разблокировать
                        </button>`
                    }
                    <button class="user-action-btn delete" onclick="deleteUser(${user.id})">
                        <i class="fas fa-trash"></i> Удалить
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Блокировка/разблокировка пользователя
async function toggleUserStatus(userId, active) {
    const action = active ? 'разблокировать' : 'заблокировать';

    if (!confirm(`Вы уверены, что хотите ${action} этого пользователя?`)) {
        return;
    }

    try {
        const response = await axios.put(`/api/admin/users/${userId}/status`, { active });

        if (response.data.success) {
            showAlert(`Пользователь успешно ${active ? 'разблокирован' : 'заблокирован'}`, 'success');
            loadUsers();
        } else {
            showAlert('Ошибка: ' + response.data.message, 'error');
        }
    } catch (error) {
        console.error('Ошибка при изменении статуса пользователя:', error);
        showAlert('Ошибка при изменении статуса пользователя', 'error');
    }
}

// Удаление пользователя
async function deleteUser(userId) {
    if (!confirm('ВНИМАНИЕ: Это действие нельзя отменить. Вы уверены, что хотите удалить этого пользователя?')) {
        return;
    }

    try {
        const response = await axios.delete(`/api/admin/users/${userId}`);

        if (response.data.success) {
            showAlert('Пользователь успешно удален', 'success');
            loadUsers();
        } else {
            showAlert('Ошибка при удалении пользователя: ' + response.data.message, 'error');
        }
    } catch (error) {
        console.error('Ошибка при удалении пользователя:', error);
        showAlert('Ошибка при удалении пользователя', 'error');
    }
}

// Отображение пагинации
function renderPagination(pagination) {
    const paginationContainer = document.getElementById('users-pagination');
    paginationContainer.innerHTML = '';

    if (!pagination || pagination.totalPages <= 1) {
        return;
    }

    totalPages = pagination.totalPages;

    // Кнопка "Назад"
    const prevBtn = document.createElement('button');
    prevBtn.className = 'page-btn';
    prevBtn.innerHTML = '<i class="fas fa-chevron-left"></i>';
    prevBtn.disabled = currentPage === 1;
    prevBtn.onclick = () => {
        if (currentPage > 1) {
            currentPage--;
            loadUsers();
        }
    };
    paginationContainer.appendChild(prevBtn);

    // Страницы
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);

    for (let i = startPage; i <= endPage; i++) {
        const pageBtn = document.createElement('button');
        pageBtn.className = `page-btn ${i === currentPage ? 'active' : ''}`;
        pageBtn.textContent = i;
        pageBtn.onclick = () => {
            currentPage = i;
            loadUsers();
        };
        paginationContainer.appendChild(pageBtn);
    }

    // Кнопка "Вперед"
    const nextBtn = document.createElement('button');
    nextBtn.className = 'page-btn';
    nextBtn.innerHTML = '<i class="fas fa-chevron-right"></i>';
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.onclick = () => {
        if (currentPage < totalPages) {
            currentPage++;
            loadUsers();
        }
    };
    paginationContainer.appendChild(nextBtn);
}

// Форматирование даты и времени
function formatDateTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}
let currentPage = 1;
let totalPages = 1;
let searchQuery = '';
let roleFilter = '';
let statusFilter = '';
let selectedUsers = new Set(); // Храним ID выбранных пользователей

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
        clearSelection();
        loadUsers();
    });

    // Очистка выбора
    document.getElementById('clear-selection').addEventListener('click', clearSelection);

    // Массовые действия
    document.getElementById('bulk-block').addEventListener('click', () => bulkAction('block'));
    document.getElementById('bulk-unblock').addEventListener('click', () => bulkAction('unblock'));
    document.getElementById('bulk-delete').addEventListener('click', () => bulkAction('delete'));

    // Выбрать все
    document.getElementById('select-all').addEventListener('change', toggleSelectAll);
}

// Очистка выбора пользователей
function clearSelection() {
    selectedUsers.clear();
    updateBulkActions();
    // Снимаем выделение со всех чекбоксов
    document.querySelectorAll('.user-checkbox').forEach(checkbox => {
        checkbox.checked = false;
        checkbox.closest('tr').classList.remove('selected');
    });
    document.getElementById('select-all').checked = false;
}

// Обновление видимости массовых действий
function updateBulkActions() {
    const bulkActions = document.getElementById('bulk-actions');
    const selectedCount = document.getElementById('selected-count');
    
    if (selectedUsers.size > 0) {
        bulkActions.style.display = 'block';
        bulkActions.classList.add('show');
        selectedCount.textContent = `Выбрано: ${selectedUsers.size}`;
    } else {
        bulkActions.classList.remove('show');
        setTimeout(() => {
            bulkActions.style.display = 'none';
        }, 300);
        selectedCount.textContent = 'Выбрано: 0';
    }
}

// Выбрать/снять выделение со всех пользователей
function toggleSelectAll() {
    const selectAll = document.getElementById('select-all');
    const checkboxes = document.querySelectorAll('.user-checkbox');
    
    checkboxes.forEach(checkbox => {
        const userId = parseInt(checkbox.dataset.userId);
        const row = checkbox.closest('tr');
        
        if (selectAll.checked) {
            checkbox.checked = true;
            selectedUsers.add(userId);
            row.classList.add('selected');
        } else {
            checkbox.checked = false;
            selectedUsers.delete(userId);
            row.classList.remove('selected');
        }
    });
    
    updateBulkActions();
}

// Обработка выбора отдельного пользователя
function handleUserSelection(userId, checkbox) {
    const row = checkbox.closest('tr');
    
    if (checkbox.checked) {
        selectedUsers.add(userId);
        row.classList.add('selected');
    } else {
        selectedUsers.delete(userId);
        row.classList.remove('selected');
        // Снимаем "выбрать все" если сняли один чекбокс
        document.getElementById('select-all').checked = false;
    }
    
    updateBulkActions();
}

// Массовое действие
async function bulkAction(action) {
    if (selectedUsers.size === 0) {
        showAlert('Выберите хотя бы одного пользователя', 'error');
        return;
    }

    const actionNames = {
        'block': 'заблокировать',
        'unblock': 'разблокировать',
        'delete': 'удалить'
    };

    const actionName = actionNames[action];
    const userCount = selectedUsers.size;
    
    if (!confirm(`Вы уверены, что хотите ${actionName} ${userCount} пользователей?`)) {
        return;
    }

    try {
        let successCount = 0;
        let errorCount = 0;
        const errors = [];
        const adminUsers = []; // Пользователи admin, которых нельзя изменить

        // Выполняем действие для каждого выбранного пользователя
        for (const userId of selectedUsers) {
            try {
                let response;

                switch (action) {
                    case 'block':
                        response = await axios.put(`/api/admin/users/${userId}/status`, { active: false });
                        break;
                    case 'unblock':
                        response = await axios.put(`/api/admin/users/${userId}/status`, { active: true });
                        break;
                    case 'delete':
                        response = await axios.delete(`/api/admin/users/${userId}`);
                        break;
                }

                if (response.data.success) {
                    successCount++;
                } else {
                    errorCount++;
                    // Проверяем, не является ли это попыткой изменить admin
                    if (response.data.message && response.data.message.includes('администратора системы')) {
                        adminUsers.push(userId);
                        errors.push(`Пользователь ID ${userId}: ${response.data.message}`);
                    } else {
                        errors.push(`Пользователь ID ${userId}: ${response.data.message}`);
                    }
                }
            } catch (error) {
                errorCount++;
                if (error.response && error.response.data && error.response.data.message) {
                    if (error.response.data.message.includes('администратора системы')) {
                        adminUsers.push(userId);
                    }
                    errors.push(`Пользователь ID ${userId}: ${error.response.data.message}`);
                } else {
                    errors.push(`Пользователь ID ${userId}: ${error.message}`);
                }
            }
        }

        // Показываем результат
        let message = '';
        let alertType = 'info';

        if (successCount > 0) {
            message += `Успешно ${actionName} ${successCount} пользователей. `;
            alertType = 'success';
        }

        if (adminUsers.length > 0) {
            message += `Пропущено ${adminUsers.length} пользователей (администраторы системы). `;
            alertType = 'warning';
        }

        if (errorCount > adminUsers.length) {
            message += `Не удалось ${actionName} ${errorCount - adminUsers.length} пользователей.`;
            alertType = 'error';
        }

        showAlert(message, alertType);

        if (errors.length > 0 && errors.length > adminUsers.length) {
            console.error('Ошибки массового действия:', errors);
        }

        // Обновляем список пользователей и очищаем выбор
        clearSelection();
        loadUsers();

    } catch (error) {
        console.error('Ошибка массового действия:', error);
        showAlert(`Ошибка при выполнении массового действия: ${error.message}`, 'error');
    }
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
        clearSelection();
        return;
    }

    users.forEach(user => {
        const isSelected = selectedUsers.has(user.id);
        const row = document.createElement('tr');
        if (isSelected) {
            row.classList.add('selected');
        }
        
        row.innerHTML = `
            <td class="checkbox-cell">
                <input type="checkbox" 
                       class="user-checkbox" 
                       data-user-id="${user.id}"
                       ${isSelected ? 'checked' : ''}
                       onchange="handleUserSelection(${user.id}, this)">
            </td>
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
        `;
        tbody.appendChild(row);
    });

    // Обновляем состояние "Выбрать все"
    const allCheckboxes = document.querySelectorAll('.user-checkbox');
    const allChecked = allCheckboxes.length > 0 && Array.from(allCheckboxes).every(cb => cb.checked);
    document.getElementById('select-all').checked = allChecked;
}

// Блокировка/разблокировка пользователя (оставляем для совместимости)
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

// Удаление пользователя (оставляем для совместимости)
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
            clearSelection();
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
            clearSelection();
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
            clearSelection();
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
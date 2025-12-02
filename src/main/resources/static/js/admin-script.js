// Глобальные переменные
let currentServiceId = null;
let currentWorkId = null;
let currentImageFile = null;

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    initializeAdminPanel();
});

// Основная функция инициализации
function initializeAdminPanel() {
    setupNavigation();
    setupEventListeners();
    loadInitialData();
}

// Настройка навигации
function setupNavigation() {
    document.querySelectorAll('.admin-nav-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            // Убираем активный класс у всех кнопок и секций
            document.querySelectorAll('.admin-nav-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.admin-section').forEach(s => s.classList.remove('active'));

            // Добавляем активный класс текущей кнопке
            this.classList.add('active');

            // Показываем соответствующую секцию
            const sectionId = this.getAttribute('data-section');
            document.getElementById(sectionId).classList.add('active');
        });
    });
}

// Настройка обработчиков событий
function setupEventListeners() {
    // Форма услуги
    document.getElementById('service-form').addEventListener('submit', handleServiceSubmit);
    document.getElementById('cancel-edit').addEventListener('click', cancelServiceEdit);

    // Форма работы
    document.getElementById('work-form').addEventListener('submit', handleWorkSubmit);
    document.getElementById('cancel-work-edit').addEventListener('click', cancelWorkEdit);

    // Кнопка выбора файла
    document.getElementById('select-file-btn').addEventListener('click', function() {
        document.getElementById('work-image-file').click();
    });

    // Кнопка очистки предпросмотра
    document.getElementById('clear-preview-btn').addEventListener('click', clearImagePreview);

    // Устанавливаем текущую дату по умолчанию
    document.getElementById('work-date').valueAsDate = new Date();

    // Инициализация загрузки файлов
    initFileUpload();
}

// Загрузка начальных данных
function loadInitialData() {
    loadServices();
    loadWorks();
}

// Инициализация загрузки файлов
function initFileUpload() {
    const fileInput = document.getElementById('work-image-file');
    const previewContainer = document.getElementById('image-preview-container');
    const previewImage = document.getElementById('image-preview');
    const fileNameSpan = document.querySelector('.file-name');

    // Обработка выбора файла
    fileInput.addEventListener('change', function(e) {
        if (this.files && this.files[0]) {
            const file = this.files[0];
            currentImageFile = file;

            // Показываем имя файла
            fileNameSpan.textContent = file.name;
            fileNameSpan.title = file.name;

            // Показываем предпросмотр для изображений
            if (file.type.startsWith('image/')) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    previewImage.src = e.target.result;
                    previewContainer.style.display = 'block';
                }
                reader.readAsDataURL(file);
            } else {
                previewContainer.style.display = 'none';
            }

            // Очищаем поле URL
            document.getElementById('work-image-url').value = '';
        }
    });

    // Drag & Drop
    const uploadContainer = document.querySelector('.file-upload-container');

    uploadContainer.addEventListener('dragover', function(e) {
        e.preventDefault();
        this.classList.add('drag-over');
    });

    uploadContainer.addEventListener('dragleave', function(e) {
        this.classList.remove('drag-over');
    });

    uploadContainer.addEventListener('drop', function(e) {
        e.preventDefault();
        this.classList.remove('drag-over');

        if (e.dataTransfer.files.length) {
            fileInput.files = e.dataTransfer.files;
            fileInput.dispatchEvent(new Event('change'));
        }
    });
}

// Очистка предпросмотра
function clearImagePreview() {
    document.getElementById('work-image-file').value = '';
    document.getElementById('image-preview').src = '';
    document.getElementById('image-preview-container').style.display = 'none';
    document.querySelector('.file-name').textContent = 'Файл не выбран';
    currentImageFile = null;
}

// === ФУНКЦИИ ДЛЯ УСЛУГ ===
// Загрузка всех услуг
async function loadServices() {
    try {
        const response = await axios.get('/api/admin/services');
        renderServicesTable(response.data);
    } catch (error) {
        console.error('Ошибка при загрузке услуг:', error);
        showAlert('Ошибка при загрузке услуг', 'error');
    }
}

// Отображение услуг в таблице
function renderServicesTable(services) {
    const tbody = document.getElementById('services-table-body');
    tbody.innerHTML = '';

    services.forEach(service => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${escapeHtml(service.title || '')}</td>
            <td>${escapeHtml(service.description || '')}</td>
            <td>${formatPrice(service.price || 0)}</td>
            <td>${service.isPopular ? '<i class="fas fa-check text-success"></i>' : '<i class="fas fa-times text-danger"></i>'}</td>
            <td>
                <div class="action-buttons">
                    <button class="action-btn btn-primary" onclick="editService(${service.id})">
                        Изменить
                    </button>
                    <button class="action-btn btn-danger" onclick="deleteService(${service.id})">
                        Удалить
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Редактирование услуги
async function editService(id) {
    try {
        const response = await axios.get(`/api/admin/services/${id}`);
        const service = response.data;

        // Заполняем форму
        document.getElementById('service-title').value = service.title || '';
        document.getElementById('service-description').value = service.description || '';
        document.getElementById('service-price').value = service.price || '';
        document.getElementById('service-unit').value = service.priceUnit || '';
        document.getElementById('service-popular').checked = service.isPopular || false;

        // Изменяем заголовок формы
        document.getElementById('form-title').textContent = 'Редактировать услугу';
        document.getElementById('save-service').innerHTML = '<i class="fas fa-save"></i> Обновить';
        document.getElementById('cancel-edit').style.display = 'inline-block';

        currentServiceId = id;

        // Прокрутка к форме
        document.getElementById('service-form').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Ошибка при загрузке услуги:', error);
        showAlert('Ошибка при загрузке услуги', 'error');
    }
}

// Удаление услуги
async function deleteService(id) {
    if (!confirm('Вы уверены, что хотите удалить эту услугу?')) {
        return;
    }

    try {
        const response = await axios.delete(`/api/admin/services/${id}`);

        if (response.data.success) {
            showAlert('Услуга успешно удалена', 'success');
            loadServices();
        } else {
            showAlert('Ошибка при удалении услуги: ' + response.data.message, 'error');
        }
    } catch (error) {
        console.error('Ошибка при удалении услуги:', error);
        showAlert('Ошибка при удалении услуги', 'error');
    }
}

// Обработка отправки формы услуги
async function handleServiceSubmit(e) {
    e.preventDefault();

    const serviceData = {
        title: document.getElementById('service-title').value || '',
        description: document.getElementById('service-description').value || '',
        price: parseFloat(document.getElementById('service-price').value) || 0,
        priceUnit: document.getElementById('service-unit').value || '',
        isPopular: document.getElementById('service-popular').checked
    };

    try {
        let response;

        if (currentServiceId) {
            // Редактирование существующей услуги
            response = await axios.put(`/api/admin/services/${currentServiceId}`, serviceData);
        } else {
            // Создание новой услуги
            response = await axios.post('/api/admin/services', serviceData);
        }

        if (response.data.success) {
            showAlert(response.data.message, 'success');
            resetServiceForm();
            loadServices();
        } else {
            showAlert('Ошибка: ' + response.data.message, 'error');
        }
    } catch (error) {
        console.error('Ошибка при сохранении услуги:', error);
        showAlert('Ошибка при сохранении услуги: ' + error.message, 'error');
    }
}

// Сброс формы услуги
function resetServiceForm() {
    document.getElementById('service-form').reset();
    document.getElementById('form-title').textContent = 'Добавить новую услугу';
    document.getElementById('save-service').innerHTML = '<i class="fas fa-save"></i> Сохранить';
    document.getElementById('cancel-edit').style.display = 'none';
    currentServiceId = null;
}

// Отмена редактирования услуги
function cancelServiceEdit() {
    resetServiceForm();
}

// === ФУНКЦИИ ДЛЯ РАБОТ ===
// Загрузка всех работ
async function loadWorks() {
    try {
        const response = await axios.get('/api/admin/works');
        renderWorksTable(response.data);
    } catch (error) {
        console.error('Ошибка при загрузке работ:', error);
        showAlert('Ошибка при загрузке работ', 'error');
    }
}

// Отображение работ в таблице
function renderWorksTable(works) {
    const tbody = document.getElementById('works-table-body');
    tbody.innerHTML = '';

    works.forEach(work => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><img src="${escapeHtml(work.imageUrl || '')}" alt="${escapeHtml(work.title || '')}" style="width: 60px; height: 60px; object-fit: cover;"></td>
            <td>${escapeHtml(work.title || '')}</td>
            <td>${formatDate(work.workDate)}</td>
            <td>${formatPrice(work.price || 0)}</td>
            <td>
                <div class="action-buttons">
                    <button class="action-btn btn-primary" onclick="editWork(${work.id})">
                        Изменить
                    </button>
                    <button class="action-btn btn-danger" onclick="deleteWork(${work.id})">
                        Удалить
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Редактирование работы
async function editWork(id) {
    try {
        const response = await axios.get(`/api/admin/works/${id}`);
        const work = response.data;

        // Заполняем форму
        document.getElementById('work-title').value = work.title || '';
        document.getElementById('work-description').value = work.description || '';
        document.getElementById('work-date').value = work.workDate || '';
        document.getElementById('work-price').value = work.price || '';

        // Для изображения
        if (work.imageUrl && work.imageUrl.startsWith('/uploads/')) {
            // Это локальный файл
            document.getElementById('work-image-url').value = '';
            document.querySelector('.file-name').textContent = 'Загруженный файл';

            // Показываем предпросмотр
            const previewImage = document.getElementById('image-preview');
            previewImage.src = work.imageUrl;
            document.getElementById('image-preview-container').style.display = 'block';

            // Добавляем checkbox для сохранения существующего изображения
            addKeepExistingImageCheckbox();
        } else {
            // Это URL
            document.getElementById('work-image-url').value = work.imageUrl || '';
            document.getElementById('image-preview-container').style.display = 'none';
            document.querySelector('.file-name').textContent = 'Файл не выбран';
        }

        // Изменяем заголовок формы
        document.getElementById('work-form-title').textContent = 'Редактировать работу';
        document.getElementById('save-work').innerHTML = '<i class="fas fa-save"></i> Обновить';
        document.getElementById('cancel-work-edit').style.display = 'inline-block';

        currentWorkId = id;
        currentImageFile = null;

        // Переключаемся на секцию работ
        document.querySelector('[data-section="works"]').click();

        // Прокрутка к форме
        document.getElementById('work-form').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Ошибка при загрузке работы:', error);
        showAlert('Ошибка при загрузке работы', 'error');
    }
}

// Добавление checkbox для сохранения существующего изображения
function addKeepExistingImageCheckbox() {
    // Удаляем старый checkbox если есть
    const oldCheckbox = document.getElementById('keep-existing-image');
    if (oldCheckbox) {
        oldCheckbox.parentNode.remove();
    }

    // Создаем новый
    const formGroup = document.createElement('div');
    formGroup.className = 'form-group';
    formGroup.innerHTML = `
        <label class="checkbox-label">
            <input type="checkbox" id="keep-existing-image" checked>
            Сохранить текущее изображение
        </label>
        <small class="form-hint">Если снимете галочку, текущее изображение будет удалено</small>
    `;

    // Вставляем перед кнопками действий
    const formActions = document.querySelector('.form-actions');
    formActions.parentNode.insertBefore(formGroup, formActions);
}

// Удаление работы
async function deleteWork(id) {
    if (!confirm('Вы уверены, что хотите удалить эту работу?')) {
        return;
    }

    try {
        const response = await axios.delete(`/api/admin/works/${id}`);

        if (response.data.success) {
            showAlert('Работа успешно удалена', 'success');
            loadWorks();
        } else {
            showAlert('Ошибка при удалении работы: ' + response.data.message, 'error');
        }
    } catch (error) {
        console.error('Ошибка при удалении работы:', error);
        showAlert('Ошибка при удалении работы', 'error');
    }
}

// Обработка отправки формы работы
async function handleWorkSubmit(e) {
    e.preventDefault();

    console.log("=== НАЧАЛО ОТПРАВКИ ФОРМЫ РАБОТЫ ===");

    const formData = new FormData();
    const title = document.getElementById('work-title').value || '';
    const description = document.getElementById('work-description').value || '';
    const workDate = document.getElementById('work-date').value || new Date().toISOString().split('T')[0];
    const price = document.getElementById('work-price').value || '0';
    const imageUrl = document.getElementById('work-image-url').value;

    console.log("Данные формы:", {
        title: title,
        description: description,
        workDate: workDate,
        price: price,
        imageUrl: imageUrl,
        hasImageFile: !!currentImageFile
    });

    formData.append('title', title);
    formData.append('description', description);
    formData.append('workDate', workDate);
    formData.append('price', price);

    // Добавляем файл или URL
    if (currentImageFile) {
        console.log('Добавляем файл:', currentImageFile.name, currentImageFile.size, currentImageFile.type);
        formData.append('imageFile', currentImageFile);
    } else if (imageUrl && imageUrl.trim() !== '') {
        console.log('Добавляем URL:', imageUrl);
        formData.append('imageUrl', imageUrl);
    }
    // Если ни файл ни URL не указаны, сервер использует изображение по умолчанию

    // Для отладки - выводим содержимое FormData
    for (let [key, value] of formData.entries()) {
        console.log(`${key}:`, value);
    }

    try {
        console.log('Отправка запроса на сервер...');
        let response;

        if (currentWorkId) {
            // Редактирование существующей работы
            console.log('Редактирование работы ID:', currentWorkId);
            response = await axios.put(`/api/admin/works/${currentWorkId}/upload`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                timeout: 30000 // 30 секунд таймаут
            });
        } else {
            // Создание новой работы
            console.log('Создание новой работы');
            response = await axios.post('/api/admin/works/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                timeout: 30000 // 30 секунд таймаут
            });
        }

        console.log('Ответ от сервера:', response.data);

        if (response.data.success) {
            console.log('Работа успешно сохранена');
            showAlert(response.data.message, 'success');
            resetWorkForm();
            await loadWorks();
        } else {
            console.error('Ошибка от сервера:', response.data);
            showAlert('Ошибка: ' + response.data.message, 'error');
        }

        console.log("=== КОНЕЦ ОТПРАВКИ ФОРМЫ РАБОТЫ (успешно) ===");
    } catch (error) {
        console.error('=== ОШИБКА ПРИ СОХРАНЕНИИ РАБОТЫ ===');
        console.error('Полная ошибка:', error);

        if (error.response) {
            // Сервер ответил с ошибкой
            console.error('Статус ошибки:', error.response.status);
            console.error('Данные ошибки:', error.response.data);
            console.error('Заголовки ошибки:', error.response.headers);

            let errorMessage = 'Ошибка сервера: ' + error.response.status;
            if (error.response.data) {
                if (error.response.data.message) {
                    errorMessage += ' - ' + error.response.data.message;
                } else if (typeof error.response.data === 'string') {
                    errorMessage += ' - ' + error.response.data;
                } else {
                    errorMessage += ' - ' + JSON.stringify(error.response.data);
                }
            }

            showAlert(errorMessage, 'error');
        } else if (error.request) {
            // Запрос был сделан, но нет ответа
            console.error('Нет ответа от сервера:', error.request);
            showAlert('Нет ответа от сервера. Проверьте подключение к сети.', 'error');
        } else {
            // Что-то пошло не так при настройке запроса
            console.error('Ошибка настройки запроса:', error.message);
            showAlert('Ошибка: ' + error.message, 'error');
        }

        // Проверяем, возможно работа всё же добавилась
        console.log('Проверяем, возможно работа добавилась несмотря на ошибку...');
        await loadWorks();

        console.log("=== КОНЕЦ ОТПРАВКИ ФОРМЫ РАБОТЫ (с ошибкой) ===");
    }
}

// Сброс формы работы
function resetWorkForm() {
    document.getElementById('work-form').reset();
    document.getElementById('work-date').valueAsDate = new Date();
    document.getElementById('work-form-title').textContent = 'Добавить новую работу';
    document.getElementById('save-work').innerHTML = '<i class="fas fa-save"></i> Сохранить работу';
    document.getElementById('cancel-work-edit').style.display = 'none';
    document.getElementById('image-preview-container').style.display = 'none';
    document.querySelector('.file-name').textContent = 'Файл не выбран';
    currentWorkId = null;
    currentImageFile = null;

    // Убираем checkbox если он есть
    const keepExistingCheckbox = document.getElementById('keep-existing-image');
    if (keepExistingCheckbox) {
        keepExistingCheckbox.parentNode.remove();
    }
}

// Отмена редактирования работы
function cancelWorkEdit() {
    resetWorkForm();
}

// === ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ===
// Показать уведомление
function showAlert(message, type = 'info') {
    // Удаляем предыдущие уведомления
    const existingAlert = document.querySelector('.alert-notification');
    if (existingAlert) {
        existingAlert.remove();
    }

    const alertDiv = document.createElement('div');
    alertDiv.className = `alert-notification alert-${type}`;
    alertDiv.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
        <span>${message}</span>
    `;

    document.body.appendChild(alertDiv);

    // Анимация появления
    setTimeout(() => {
        alertDiv.classList.add('show');
    }, 10);

    // Автоматическое скрытие через 5 секунд
    setTimeout(() => {
        alertDiv.classList.remove('show');
        setTimeout(() => {
            alertDiv.remove();
        }, 300);
    }, 5000);
}

// Форматирование даты
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU');
}

// Форматирование цены
function formatPrice(price) {
    return new Intl.NumberFormat('ru-RU', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2
    }).format(price) + ' ₽';
}

// Экранирование HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
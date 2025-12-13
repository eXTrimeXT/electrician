# ⚡ Профессиональный электрик

Веб-приложение для предоставления услуг электрика с административной панелью, системой отзывов и управлением контентом.

## 💡 Пример реализации

<table>
  <tr>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/home.png" width="100%"></td>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/home_added.png" width="100%"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/login.png" width="100%"></td>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/user_profile.png" width="100%"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/register.png" width="100%"></td>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/register_mail.png" width="100%"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/reviews.png" width="100%"></td>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/reviews_added.png" width="100%"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/admin_service.png" width="100%"></td>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/admin_reviews.png" width="100%"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/admin_work.png" width="100%"></td>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/admin_work_added.png" width="100%"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/admin_users.png" width="100%"></td>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/admin_users_edit.png" width="100%"></td>
  </tr>
</table>

## ✨ Особенности

- **Публичная часть**:
    - Отображение услуг и цен с разделением на популярные и полный прайс
    - Галерея выполненных работ с фотографиями
    - Контактная информация
    - Система отзывов с рейтингом и ответами администратора

- **Пользовательский функционал**:
    - Регистрация и авторизация
    - Личный кабинет
    - Добавление и редактирование отзывов
    - Просмотр всех отзывов

- **Административная панель**:
    - Управление услугами (добавление, редактирование, удаление)
    - Управление галереей работ с загрузкой изображений
    - Модерация отзывов и ответы пользователям

## 🛠 Технологии

- **Backend**: Spring Boot, Thymeleaf, Spring Security
- **Frontend**: HTML5, CSS3, JavaScript
- **База данных**: PostgreSQL (конфигурируется в application.properties)
- **Дополнительно**:
    - Font Awesome для иконок
    - Responsive дизайн
    - Drag & Drop загрузка файлов
    - Валидация форм на клиенте и сервере

## 📁 Структура проекта

<table>
  <tr>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/struct_1.png" width="100%"></td>
    <td><img src="https://github.com/eXTrimeXT/electrician/blob/main/md_screens/struct_2.png" width="100%"></td>
  </tr>
</table>

```
electrician-project/
├── src/main/java/com/extrime/electrician/
│   ├── config/
│   │   ├── Config.java
│   │   ├── ConfigPostgres.java
│   │   ├── DatabaseInitializer.java
│   │   └── FileUploadConfig.java
│   ├── controller/
│   │   ├── AdminApiController.java
│   │   ├── AdminController.java
│   │   ├── AuthController.java
│   │   ├── CustomErrorController.java
│   │   ├── HomeController.java
│   │   ├── PasswordResetController.java
│   │   ├── RegisterController.java
│   │   └── ReviewController.java
│   ├── dao/
│   │   ├── EmailVerificationDAO.java
│   │   ├── ReviewDAO.java
│   │   ├── ServiceDAO.java
│   │   ├── UserDAO.java
│   │   └── WorkDAO.java
│   ├── model/
│   │   ├── ContactInfo.java
│   │   ├── EmailVerification.java
│   │   ├── OurService.java
│   │   ├── PasswordResetToken.java
│   │   ├── Review.java
│   │   ├── User.java
│   │   └── Work.java
│   ├── service/
│   │   ├── email/
│   │   │   ├── EmailService.java
│   │   │   └── EmailVerificationService.java
│   │   ├── AuthService.java
│   │   ├── FileStorageService.java
│   │   ├── PasswordService.java
│   │   └── RegisterService.java
│   └── ElectricianApplication.java
│
└── src/main/resources/
    ├── static/
    │   ├── css/
    │   │   ├── admin.css
    │   │   ├── error.css
    │   │   ├── file-upload.css
    │   │   ├── home.css
    │   │   ├── login.css
    │   │   ├── password-reset.css
    │   │   ├── profile.css
    │   │   ├── register.css
    │   │   ├── reviews.css
    │   │   └── verification.css
    │   ├── js/
    │   │   ├── admin-script.js
    │   │   ├── password-reset-script.js
    │   │   ├── register-script.js
    │   │   ├── reviews-script.js
    │   │   └── verification-script.js
    │   └── uploads/
    │
    ├── templates/
    │   ├── error/
    │   │   ├── 404.html
    │   │   ├── 500.html
    │   │   └── error.html
    │   ├── admin.html
    │   ├── home.html
    │   ├── login.html
    │   ├── password-error.html
    │   ├── password-forgot.html
    │   ├── password-reset.html
    │   ├── password-success.html
    │   ├── profile.html
    │   ├── register.html
    │   ├── reviews.html
    │   └── verification.html
    │
    ├── application.yml
    ├── create_db.sql
    ├── table_email.sql
    ├── table_reviews.sql
    ├── table_services.sql
    ├── table_users.sql
    ├── table_works.sql
    └── view.sql
```

## 🚀 Быстрый старт

1. **Клонировать репозиторий**:
   ```bash
   git clone https://github.com/eXTrimeXT/electrician.git
   ```

2. **Настроить базу данных**:
    - Создать БД PostgreSQL
    - Обновить настройки в `application.yml`

3. **Запустить приложение**:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
   
## 📦 Запуск через Docker
1. **Сборка и запуск**:
```
# Собрать и запустить все сервисы в фоновом режиме
docker-compose up -d --build

# Остановка
docker-compose down

# Просмотр логов
docker-compose logs -f app
```

2. **Проверка работы**:
```
# Проверить статус контейнеров
docker ps

# Проверить логи приложения
docker logs electrician-app

# Проверить доступность
curl http://localhost:8081
```

3. **Полезные команды**:
```
# Остановить все контейнеры
docker-compose down

# Очистить все (осторожно!)
docker-compose down -v

# Пересобрать без кэша
docker-compose build --no-cache

# Войти в контейнер с приложением
docker exec -it electrician-app sh

# Проверить подключение к БД из контейнера
docker exec -it electrician-db psql -U postgres -d electrician_db
```

### **Доступ к приложению**:
- Главная страница: http://localhost:8080
- Админ панель: http://localhost:8080/admin
- Страница отзывов: http://localhost:8080/reviews
- Страница входа: http://localhost:8080/login
- Страница регистрации: http://localhost:8080/register

## 🔐 Доступ по умолчанию

- **Администратор**:
    - Логин: `admin`
    - Пароль: `admin` (изменить после первого входа)

- **Обычный пользователь**:
    - Регистрация доступна по ссылке "/register"

## 📱 Адаптивность

Приложение полностью адаптивно и корректно отображается на:
- Десктопах и ноутбуках
- Планшетах
- Мобильных устройствах

## 🎨 Особенности дизайна

- Современный минималистичный интерфейс
- Использование градиентов и теней
- Анимации и плавные переходы
- Интуитивная навигация
- Цветовая схема: синие оттенки с акцентными цветами для действий

## 🔧 Настройка и кастомизация

### Контактная информация
Изменяются через админ-панель или непосредственно в контроллерах:
- Телефон
- Email
- Адрес
- Часы работы

### Услуги и работы
- Добавляются/редактируются через админ-панель
- Поддержка загрузки изображений
- Автоматическое форматирование цен и дат

## 🤝 Поддержка

При возникновении вопросов или обнаружении ошибок создавайте issue в репозитории проекта.

---
**Версия**: 1.0.0  
**Последнее обновление**: Декабрь 2025
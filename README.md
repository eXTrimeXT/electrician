# ⚡ Профессиональный электрик

Веб-приложение для предоставления услуг электрика с административной панелью, системой отзывов и управлением контентом.

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

```
electrician-project/
│
├── java/
│   └── com/
│       └── extrime/
│           └── electrician/
│               ├── config/
│               │   ├── ConfigPostgres
│               │   ├── DatabaseInitializer
│               │   └── FileUploadConfig
│               │
│               ├── controller/
│               │   ├── AdminApiController
│               │   ├── AdminController
│               │   ├── AuthController
│               │   ├── CustomErrorController.java
│               │   ├── HomeController
│               │   ├── RegisterController
│               │   └── ReviewController
│               │
│               ├── dao/
│               │   ├── ReviewDAO
│               │   ├── ServiceDAO
│               │   ├── UserDAO
│               │   └── WorkDAO
│               │
│               ├── model/
│               │   ├── ContactInfo
│               │   ├── OurService
│               │   ├── Review
│               │   ├── User
│               │   └── Work
│               │
│               ├── service/
│               │   ├── AuthService
│               │   ├── FileStorageService
│               │   ├── PasswordService
│               │   └── RegisterService
│               │
│               └── ElectricianApplication.java
│
├── resources/
│   ├── static/
│   │   ├── css/
│   │   │   ├── admin.css
│   │   │   ├── error.css
│   │   │   ├── file-upload.css
│   │   │   ├── home.css
│   │   │   ├── login.css
│   │   │   ├── profile.css
│   │   │   ├── register.css
│   │   │   └── reviews.css
│   │   │
│   │   ├── images/
│   │   ├── js/
│   │   │   ├── admin-script.js
│   │   │   ├── register-script.js
│   │   │   └── reviews-script.js
│   │   │
│   │   └── uploads/
│   │
│   ├── templates/
│   │   ├── error111/
│   │   │   └── ...
│   │   ├── 404.html
│   │   ├── 500.html
│   │   ├── error.html
│   │   ├── admin.html
│   │   ├── home.html
│   │   ├── login.html
│   │   ├── profile.html
│   │   ├── register.html
│   │   └── reviews.html
│   │
│   ├── application.yml
│   ├── postgres.sql
│   ├── postgres_users.sql
│   ├── postgres_works.sql
│   ├── reviews.sql
│   └── view.sql
```

## 🚀 Быстрый старт

1. **Клонировать репозиторий**:
   ```bash
   git clone https://github.com/eXTrimeXT/electrician.git
   ```

2. **Настроить базу данных**:
    - Создать БД PostgreSQL
    - Обновить настройки в `application.properties`

3. **Запустить приложение**:
   ```bash
   mvn spring-boot:run
   ```

4. **Доступ к приложению**:
    - Главная страница: `http://localhost:8080`
    - Админ панель: `http://localhost:8080/admin`
    - Страница отзывов: `http://localhost:8080/reviews`

## 🔐 Доступ по умолчанию

- **Администратор**:
    - Логин: `admin`
    - Пароль: `admin` (изменить после первого входа)

- **Обычный пользователь**:
    - Регистрация доступна по ссылке "Зарегистрироваться"

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
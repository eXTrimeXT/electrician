-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    role VARCHAR(20) DEFAULT 'USER',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание индекса для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Добавление стандартного администратора
-- Пароль: admin123 (хеш SHA-256)
INSERT INTO users (username, password, email, role)
VALUES ('admin', 'admin', 'admin@example.com', 'ADMIN')
ON CONFLICT (username) DO NOTHING;
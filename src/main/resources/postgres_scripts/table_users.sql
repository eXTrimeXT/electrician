---- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    role VARCHAR(20) DEFAULT 'USER',
    active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false, -- Добавляем эту колонку
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание индекса для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
-- Создаем индекс для быстрого поиска по email
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
-- Создаем индекс для email_verified для быстрой фильтрации
CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified) WHERE email_verified = false;
-- Создаем индекс для active, для email_verifications
CREATE INDEX IF NOT EXISTS idx_users_active ON users(active);

-- Создаем администратора
INSERT INTO users (username, password, email, role)
VALUES ('admin', 'hash', 'admin@example.com', 'ADMIN')
ON CONFLICT (username) DO NOTHING;
---- Создание таблицы пользователей
--CREATE TABLE IF NOT EXISTS users (
--    id SERIAL PRIMARY KEY,
--    username VARCHAR(50) UNIQUE NOT NULL,
--    password VARCHAR(100) NOT NULL,
--    email VARCHAR(100) UNIQUE,
--    role VARCHAR(20) DEFAULT 'USER',
--    active BOOLEAN DEFAULT true,
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--);
--
---- Создание индекса для быстрого поиска
--CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
--
---- Добавление стандартного администратора
---- Пароль: admin123 (хеш SHA-256)
--INSERT INTO users (username, password, email, role)
--VALUES ('admin', 'admin', 'admin@example.com', 'ADMIN')
--ON CONFLICT (username) DO NOTHING;

-- Добавляем колонку email_verified если её нет
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'users' AND column_name = 'email_verified') THEN
        ALTER TABLE users ADD COLUMN email_verified BOOLEAN DEFAULT false;
    END IF;
END $$;

-- Проверяем наличие колонки и обновляем схему таблицы
-- Убедитесь, что таблица имеет все необходимые колонки
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

-- Если таблица уже существует, добавляем недостающие колонки
DO $$
BEGIN
    -- Добавляем email_verified если её нет
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'users' AND column_name = 'email_verified') THEN
        ALTER TABLE users ADD COLUMN email_verified BOOLEAN DEFAULT false;
    END IF;

    -- Добавляем updated_at если её нет
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'users' AND column_name = 'updated_at') THEN
        ALTER TABLE users ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;

-- Обновляем существующую запись админа
UPDATE users
SET email_verified = true,
    updated_at = CURRENT_TIMESTAMP
WHERE username = 'admin';

-- Создаем индекс для быстрого поиска по email
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Создаем индекс для email_verified для быстрой фильтрации
CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified) WHERE email_verified = false;
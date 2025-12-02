-- Создание базы данных
-- CREATE DATABASE electrician_db
--     WITH
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'en_US.UTF-8'
--     LC_CTYPE = 'en_US.UTF-8'
--     TABLESPACE = pg_default
--     CONNECTION LIMIT = -1;

-- Создание таблицы услуг
CREATE TABLE IF NOT EXISTS services (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    price_unit VARCHAR(50) DEFAULT 'за штуку',
    is_popular BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Добавление тестовых данных (одна популярная услуга)
INSERT INTO services (title, description, price, price_unit, is_popular) VALUES
('Установка розетки', 'Монтаж одинарной розетки с подключением', 800.00, 'за штуку', true),
('Замена выключателя', 'Демонтаж старого и установка нового выключателя', 600.00, 'за штуку', false),
('Монтаж светильника', 'Установка потолочного светильника с подключением', 1200.00, 'за штуку', false),
('Замена электропроводки', 'Полная замена проводки в комнате', 15000.00, 'за комнату', true),
('Установка автомата', 'Монтаж автоматического выключателя в щитке', 500.00, 'за штуку', false);
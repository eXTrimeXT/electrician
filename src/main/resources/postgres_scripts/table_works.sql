-- Создание таблицы работ
CREATE TABLE IF NOT EXISTS works (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    work_date DATE NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Добавление тестовых работ
INSERT INTO works (title, description, work_date, price, image_url) VALUES
('Замена электропроводки в квартире', 'Полная замена старой проводки, установка новых автоматов и УЗО, монтаж современных розеток и выключателей', '2024-03-15', 25000.00, '/static/images/EATB.jpeg'),
('Установка точечного освещения', 'Монтаж 12 точечных светильников в натяжном потолке, установка диммера и подключение к системе умного дома', '2024-03-10', 12500.00, '/static/images/EATB.jpeg'),
('Сборка электрощита', 'Комплектация и монтаж распределительного щита с автоматами на 24 группы, установка реле напряжения и УЗИП', '2024-03-05', 18000.00, '/static/images/EATB.jpeg');
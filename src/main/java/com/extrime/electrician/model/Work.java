package com.extrime.electrician.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Work {
    private Long id;
    private String title;
    private String description;
    private LocalDate workDate;
    private Double price;
    private String imageUrl;

    // Конструкторы
    public Work() {}

    public Work(Long id, String title, String description, LocalDate workDate, Double price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.workDate = workDate;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Метод для получения форматированной даты
    public String getFormattedDate() {
        if (workDate == null) {
            return "";
        }
        return workDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    // Проверка, является ли изображение локальным (загруженным)
    public boolean isLocalImage() {
        return imageUrl != null && (imageUrl.startsWith("/static/uploads/") || imageUrl.startsWith("/static/"));
    }

    // Получение имени файла из URL
    public String getImageFileName() {
        if (imageUrl == null) return "";
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}
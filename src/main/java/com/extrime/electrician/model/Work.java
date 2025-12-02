package com.extrime.electrician.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Setter
@Getter
public class Work {
    // Геттеры и сеттеры
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

    // Метод для получения форматированной даты
    public String getFormattedDate() {
        if (workDate == null) return "";
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
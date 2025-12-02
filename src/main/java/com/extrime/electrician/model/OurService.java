package com.extrime.electrician.model;

// Наши услуги(service) на сайте
public class OurService {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String priceUnit;
    private Boolean isPopular;

    // Конструкторы
    public OurService() {}

    public OurService(Long id, String title, String description, Double price, String priceUnit, Boolean isPopular) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.priceUnit = priceUnit;
        this.isPopular = isPopular;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }

    public Boolean getIsPopular() { return isPopular; }
    public void setIsPopular(Boolean isPopular) { this.isPopular = isPopular; }
}
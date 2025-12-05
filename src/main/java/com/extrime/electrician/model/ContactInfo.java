package com.extrime.electrician.model;

public class ContactInfo {
    private String address;
    private String phone;
    private String email;
    private String workingHours;

    public ContactInfo() {
        // Значения по умолчанию
        this.address = "г. Нижний Новгород";
        this.phone = "+7 (950) 621-97-57";
        this.email = "electrician@example.com";
        this.workingHours = "Пн-Пт: 8:00-17:00";
    }

    // Геттеры и сеттеры
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }
}
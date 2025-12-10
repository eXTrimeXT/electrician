package com.extrime.electrician.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContactInfo {
    private String address;
    private String phone;
    private String email;
    private String workingHours;

    public ContactInfo() {
        // Значения по умолчанию
        this.address = "г. Нижний Новгород";
        this.phone = "+79506219757"; // На главной странице, также изменять ссылку tel:
        this.email = "electric252.com@gmail.com";
        this.workingHours = "Пн-Вс: 08:00-20:00";
    }
}
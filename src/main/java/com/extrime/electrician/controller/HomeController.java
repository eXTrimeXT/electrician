package com.extrime.electrician.controller;

import com.extrime.electrician.dao.ServiceDAO;
import com.extrime.electrician.dao.UserDAO;
import com.extrime.electrician.dao.WorkDAO;
import com.extrime.electrician.model.ContactInfo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    private final ServiceDAO serviceDAO;
    private final WorkDAO workDAO;

    @Autowired
    public HomeController(ServiceDAO serviceDAO, WorkDAO workDAO, UserDAO userDAO) {
        this.serviceDAO = serviceDAO;
        this.workDAO = workDAO;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Устанавливаем заголовок страницы
        model.addAttribute("pageTitle", "Электрик - профессиональные услуги");

        // Получаем услуги из БД
        model.addAttribute("services", serviceDAO.getAllServices());
        model.addAttribute("popularServices", serviceDAO.getPopularServices());

        // Получаем работы из БД
        model.addAttribute("works", workDAO.getAllWorks());

        // Добавляем контактную информацию
        model.addAttribute("contactInfo", new ContactInfo());

        return "home";
    }
}
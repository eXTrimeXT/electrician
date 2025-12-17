package com.extrime.electrician.controller;

import com.extrime.electrician.dao.ServiceDAO;
import com.extrime.electrician.dao.TelegramPostDAO;
import com.extrime.electrician.dao.WorkDAO;
import com.extrime.electrician.model.ContactInfo;
import com.extrime.electrician.service.telegram.TelegramChannelService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ServiceDAO serviceDAO;
    private final WorkDAO workDAO;
    private final TelegramChannelService telegramChannelService;
    private final TelegramPostDAO telegramPostDAO;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Электрик - профессиональные услуги");
        model.addAttribute("services", serviceDAO.getAllServices());
        model.addAttribute("popularServices", serviceDAO.getPopularServices());
        model.addAttribute("works", workDAO.getAllWorks());
        model.addAttribute("contactInfo", new ContactInfo());

        // Добавляем информацию о Telegram канале
        model.addAttribute("channelInfo", telegramChannelService.getChannelInfo());

        // Добавляем информацию про последний пост + общее количество постов
         model.addAttribute("telegramPost", telegramPostDAO.getLatestPost());
         model.addAttribute("postsCount", telegramPostDAO.getCount());

         return "home";
    }
}
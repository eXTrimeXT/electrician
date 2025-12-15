package com.extrime.electrician.controller;

import com.extrime.electrician.dao.ReviewDAO;
import com.extrime.electrician.model.Review;
import com.extrime.electrician.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewDAO reviewDAO;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    public ReviewController(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    @GetMapping
    public String reviewsPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Отзывы клиентов");
        User user = (User) session.getAttribute("user");
        model.addAttribute("isLoggedIn", user != null);
        model.addAttribute("isAdmin", user != null && "ADMIN".equals(user.getRole()));
        model.addAttribute("reviews", reviewDAO.findAllActive());
        // Форматируем даты для отображения
        model.addAttribute("formatter", formatter);

        return "reviews";
    }

    @PostMapping("/add")
    public String addReview(@RequestParam Integer rating,
                            @RequestParam String comment,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Для добавления отзыва необходимо авторизоваться");
            return "redirect:/login";
        }

        // Проверка валидности рейтинга
        if (rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute("error", "Рейтинг должен быть от 1 до 5");
            return "redirect:/reviews";
        }

        // Создаем новый отзыв
        Review review = new Review();
        review.setUserId(user.getId());
        review.setUsername(user.getUsername());
        review.setRating(rating);
        review.setComment(comment);

        try {
            reviewDAO.save(review);
            redirectAttributes.addFlashAttribute("success", "Отзыв успешно добавлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении отзыва");
        }

        return "redirect:/reviews";
    }

    @PostMapping("/{id}/edit")
    public String editReview(@PathVariable Long id,
                             @RequestParam Integer rating,
                             @RequestParam String comment,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        Review review = reviewDAO.findById(id);

        // Проверка прав
        if (user == null || (!"ADMIN".equals(user.getRole()) && !user.getId().equals(review.getUserId()))) {
            redirectAttributes.addFlashAttribute("error", "У вас нет прав для редактирования этого отзыва");
            return "redirect:/reviews";
        }

        review.setRating(rating);
        review.setComment(comment);
        reviewDAO.update(review);

        redirectAttributes.addFlashAttribute("success", "Отзыв обновлен");
        return "redirect:/reviews";
    }

    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        Review review = reviewDAO.findById(id);

        // Проверка прав
        if (user == null || (!"ADMIN".equals(user.getRole()) && !user.getId().equals(review.getUserId()))) {
            redirectAttributes.addFlashAttribute("error", "У вас нет прав для удаления этого отзыва");
            return "redirect:/reviews";
        }

        reviewDAO.delete(id);
        redirectAttributes.addFlashAttribute("success", "Отзыв удален");
        return "redirect:/reviews";
    }

    @PostMapping("/{id}/admin-response")
    public String addAdminResponse(@PathVariable Long id,
                                   @RequestParam String response,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        reviewDAO.addAdminResponse(id, response);
        redirectAttributes.addFlashAttribute("success", "Ответ добавлен");
        return "redirect:/reviews";
    }

    @PostMapping("/{id}/delete-response")
    public String deleteAdminResponse(@PathVariable Long id,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        // Проверка прав администратора
        if (user == null || !"ADMIN".equals(user.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Только администратор может удалять ответы");
            return "redirect:/reviews";
        }

        reviewDAO.addAdminResponse(id, null);
        redirectAttributes.addFlashAttribute("success", "Ответ удален");
        return "redirect:/reviews";
    }
}
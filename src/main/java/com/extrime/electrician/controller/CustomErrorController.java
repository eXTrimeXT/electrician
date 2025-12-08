//package com.extrime.electrician.controller;
//
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.boot.webmvc.error.ErrorController;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Controller
//@RequestMapping("/error")
//public class CustomErrorController implements ErrorController {
//
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
//
//    @GetMapping
//    public String handleError(HttpServletRequest request, Model model) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
//        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
//
//        if (status != null) {
//            int statusCode = Integer.parseInt(status.toString());
//
//            if (statusCode == HttpStatus.NOT_FOUND.value()) {
//                return "error/404";
//            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
//                return "error/500";
//            }
//        }
//
//        // Общая страница ошибки
//        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
//        model.addAttribute("status", status != null ? status : "Неизвестно");
//        model.addAttribute("error", message != null ? message : "Произошла ошибка");
//        model.addAttribute("path", path != null ? path : request.getRequestURI());
//
//        return "error";
//    }
//
//    @GetMapping("/404")
//    public String error404(Model model) {
//        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
//        model.addAttribute("status", "404");
//        model.addAttribute("error", "Страница не найдена");
//        return "error/404";
//    }
//
//    @GetMapping("/500")
//    public String error500(Model model) {
//        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
//        model.addAttribute("status", "500");
//        model.addAttribute("error", "Внутренняя ошибка сервера");
//        return "error/500";
//    }
//}
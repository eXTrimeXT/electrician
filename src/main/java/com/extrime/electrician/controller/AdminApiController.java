package com.extrime.electrician.controller;

import com.extrime.electrician.dao.ServiceDAO;
import com.extrime.electrician.dao.UserDAO;
import com.extrime.electrician.dao.WorkDAO;
import com.extrime.electrician.model.OurService;
import com.extrime.electrician.model.User;
import com.extrime.electrician.model.Work;
import com.extrime.electrician.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {
    private final ServiceDAO serviceDAO;
    private final WorkDAO workDAO;
    private final FileStorageService fileStorageService;
    private final UserDAO userDAO;

    @Autowired
    public AdminApiController(ServiceDAO serviceDAO, WorkDAO workDAO, FileStorageService fileStorageService, UserDAO userDAO) {
        this.serviceDAO = serviceDAO;
        this.workDAO = workDAO;
        this.fileStorageService = fileStorageService;
        this.userDAO = userDAO;
    }

    // Проверка авторизации для всех API методов
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("isAuthenticated") != null;
    }


    //=== API для услуг ===
    // Получить все услуги
    @GetMapping("/services")
    public ResponseEntity<?> getAllServices(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        List<OurService> services = serviceDAO.getAllServices();
        return ResponseEntity.ok(services);
    }

    // Получить услугу по ID
    @GetMapping("/services/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        OurService service = serviceDAO.getServiceById(id);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Услуга не найдена");
        }

        return ResponseEntity.ok(service);
    }

    // Создать новую услугу
    @PostMapping("/services")
    public ResponseEntity<?> createService(@RequestBody Map<String, Object> serviceData, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        try {
            OurService service = new OurService();
            service.setTitle((String) serviceData.get("title"));
            service.setDescription((String) serviceData.get("description"));
            service.setPrice(Double.parseDouble(serviceData.get("price").toString()));
            service.setPriceUnit((String) serviceData.get("priceUnit"));
            service.setIsPopular(Boolean.parseBoolean(serviceData.get("isPopular").toString()));

            Long id = serviceDAO.addService(service);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Услуга успешно добавлена");
            response.put("id", id);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при добавлении услуги: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Обновить услугу
    @PutMapping("/services/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody Map<String, Object> serviceData, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        try {
            OurService service = serviceDAO.getServiceById(id);
            if (service == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Услуга не найдена");
            }

            service.setTitle((String) serviceData.get("title"));
            service.setDescription((String) serviceData.get("description"));
            service.setPrice(Double.parseDouble(serviceData.get("price").toString()));
            service.setPriceUnit((String) serviceData.get("priceUnit"));
            service.setIsPopular(Boolean.parseBoolean(serviceData.get("isPopular").toString()));

            boolean success = serviceDAO.updateService(service);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Услуга успешно обновлена" : "Не удалось обновить услугу");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при обновлении услуги: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Удалить услугу
    @DeleteMapping("/services/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        try {
            boolean success = serviceDAO.deleteService(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Услуга успешно удалена" : "Не удалось удалить услугу");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при удалении услуги: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    // === API для работ ===
    // Получить все работы
    @GetMapping("/works")
    public ResponseEntity<?> getAllWorks(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        List<Work> works = workDAO.getAllWorks();
        return ResponseEntity.ok(works);
    }

    // Получить работу по ID
    @GetMapping("/works/{id}")
    public ResponseEntity<?> getWorkById(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        Work work = workDAO.getWorkById(id);
        if (work == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Работа не найдена");
        }

        return ResponseEntity.ok(work);
    }

    // Создать новую работу
    @PostMapping("/works")
    public ResponseEntity<?> createWork(@RequestBody Map<String, Object> workData, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        try {
            Work work = new Work();
            work.setTitle((String) workData.get("title"));
            work.setDescription((String) workData.get("description"));
            work.setWorkDate(LocalDate.parse((String) workData.get("workDate")));
            work.setPrice(Double.parseDouble(workData.get("price").toString()));
            work.setImageUrl((String) workData.get("imageUrl"));

            Long id = workDAO.addWork(work);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Работа успешно добавлена");
            response.put("id", id);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при добавлении работы: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Обновить работу
    @PutMapping("/works/{id}")
    public ResponseEntity<?> updateWork(@PathVariable Long id, @RequestBody Map<String, Object> workData, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        try {
            Work work = workDAO.getWorkById(id);
            if (work == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Работа не найдена");
            }

            work.setTitle((String) workData.get("title"));
            work.setDescription((String) workData.get("description"));
            work.setWorkDate(LocalDate.parse((String) workData.get("workDate")));
            work.setPrice(Double.parseDouble(workData.get("price").toString()));
            work.setImageUrl((String) workData.get("imageUrl"));

            boolean success = workDAO.updateWork(work);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Работа успешно обновлена" : "Не удалось обновить работу");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при обновлении работы: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // === API для работ с загрузкой файлов ===
    // Создать новую работу с загрузкой изображения
    @PostMapping("/works/upload")
    public ResponseEntity<?> createWorkWithUpload(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("workDate") String workDate,
            @RequestParam("price") String priceStr,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            HttpSession session) {

        if (!isAuthenticated(session)) {
            System.out.println("Пользователь не авторизован");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        Work work = new Work();
        Map<String, Object> response = new HashMap<>();

        try {
            // Устанавливаем значения с обработкой null
            work.setTitle(title != null ? title.trim() : "");
            work.setDescription(description != null ? description.trim() : "");

            // Обработка даты
            try {
                if (workDate != null && !workDate.trim().isEmpty()) {
                    work.setWorkDate(LocalDate.parse(workDate));
                } else {
                    work.setWorkDate(LocalDate.now());
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга даты: " + e.getMessage());
                work.setWorkDate(LocalDate.now());
            }

            // Обработка цены
            try {
                if (priceStr != null && !priceStr.trim().isEmpty()) {
                    work.setPrice(Double.parseDouble(priceStr.trim()));
                } else {
                    work.setPrice(0.0);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка парсинга цены: " + e.getMessage());
                work.setPrice(0.0);
            }

            // Обработка изображения
            String finalImageUrl = null;

            if (imageFile != null && !imageFile.isEmpty()) {
                System.out.println(
                        "Загрузка файла: " + imageFile.getOriginalFilename() +
                                ", size: " + imageFile.getSize() +
                                ", type: " + imageFile.getContentType()
                );

                try {
                    finalImageUrl = fileStorageService.storeFile(imageFile);
                    System.out.println("Файл сохранен: " + finalImageUrl);
                } catch (IOException e) {
                    System.err.println("Ошибка сохранения файла: " + e.getMessage());
                    finalImageUrl = "/static/images/default-work.jpg";
                }
            } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                System.out.println("Используем URL: " + imageUrl);
                finalImageUrl = imageUrl.trim();
            } else {
                System.out.println("Используем изображение по умолчанию");
                finalImageUrl = "/static/images/default-work.jpg";
            }

            work.setImageUrl(finalImageUrl);
            System.out.println("Данные для сохранения:");
            System.out.println("- title: " + work.getTitle());
            System.out.println("- description: " + work.getDescription());
            System.out.println("- workDate: " + work.getWorkDate());
            System.out.println("- price: " + work.getPrice());
            System.out.println("- imageUrl: " + work.getImageUrl());

            System.out.println("Сохранение работы в БД...");
            Long id = workDAO.addWork(work);
            System.out.println("Работа сохранена с ID: " + id);

            response.put("success", true);
            response.put("message", "Работа успешно добавлена");
            response.put("id", id);
            response.put("imageUrl", work.getImageUrl());

            System.out.println("Отправка успешного ответа: " + response);
            System.out.println("=== КОНЕЦ СОЗДАНИЯ РАБОТЫ ===");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("КРИТИЧЕСКАЯ ОШИБКА при добавлении работы: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Ошибка при добавлении работы: " + e.getMessage());
            response.put("error", e.toString());

            System.out.println("=== КОНЕЦ С ОШИБКОЙ ===");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Обновить работу с загрузкой изображения
    @PutMapping("/works/{id}/upload")
    public ResponseEntity<?> updateWorkWithUpload(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("workDate") String workDate,
            @RequestParam("price") Double price,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "keepExistingImage", defaultValue = "false") boolean keepExistingImage,
            HttpSession session) {

        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        try {
            Work existingWork = workDAO.getWorkById(id);
            if (existingWork == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Работа не найдена"));
            }

            // Сохраняем старое изображение на случай, если нужно будет удалить
            String oldImageUrl = existingWork.getImageUrl();

            existingWork.setTitle(title);
            existingWork.setDescription(description);
            existingWork.setWorkDate(LocalDate.parse(workDate));
            existingWork.setPrice(price);

            // Обработка изображения
            String newImageUrl = null;

            if (imageFile != null && !imageFile.isEmpty()) {
                // Проверяем, что это изображение
                if (!fileStorageService.isImageFile(imageFile)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success", false, "message", "Файл должен быть изображением"));
                }

                // Проверяем размер файла
                if (imageFile.getSize() > fileStorageService.getMaxFileSize()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success", false, "message", "Файл слишком большой (максимум 20MB)"));
                }

                // Сохраняем новый файл
                newImageUrl = fileStorageService.storeFile(imageFile);
                existingWork.setImageUrl(newImageUrl);

                // Удаляем старое изображение, если оно локальное
                if (!keepExistingImage && oldImageUrl != null && oldImageUrl.startsWith("/uploads/")) {
                    fileStorageService.deleteFile(oldImageUrl);
                }
            } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                // Используем новый URL
                existingWork.setImageUrl(imageUrl);

                // Удаляем старое локальное изображение, если нужно
                if (!keepExistingImage && oldImageUrl != null && oldImageUrl.startsWith("/uploads/")) {
                    fileStorageService.deleteFile(oldImageUrl);
                }
            } else if (keepExistingImage) {
                // Оставляем существующее изображение
                existingWork.setImageUrl(oldImageUrl);
            } else {
                // Используем изображение по умолчанию
                existingWork.setImageUrl("/static/images/default-work.jpeg");

                // Удаляем старое локальное изображение
                if (oldImageUrl != null && oldImageUrl.startsWith("/uploads/")) {
                    fileStorageService.deleteFile(oldImageUrl);
                }
            }

            boolean success = workDAO.updateWork(existingWork);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Работа успешно обновлена" : "Не удалось обновить работу");
            response.put("imageUrl", existingWork.getImageUrl());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при обновлении работы: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Удалить работу с удалением изображения
    @DeleteMapping("/works/{id}")
    public ResponseEntity<?> deleteWork(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        try {
            // Получаем работу для удаления изображения
            Work work = workDAO.getWorkById(id);
            if (work == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Работа не найдена"));
            }

            // Удаляем изображение, если оно локальное
            if (work.getImageUrl() != null && work.getImageUrl().startsWith("/uploads/")) {
                fileStorageService.deleteFile(work.getImageUrl());
            }

            // Удаляем работу из базы данных
            boolean success = workDAO.deleteWork(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Работа успешно удалена" : "Не удалось удалить работу");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при удалении работы: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Загрузка изображения отдельно
    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile imageFile, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        try {
            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Файл не выбран"));
            }

            // Проверяем, что это изображение
            if (!fileStorageService.isImageFile(imageFile)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Файл должен быть изображением"));
            }

            // Проверяем размер файла
            if (imageFile.getSize() > fileStorageService.getMaxFileSize()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Файл слишком большой (максимум 20MB)"));
            }

            // Сохраняем файл
            String imageUrl = fileStorageService.storeFile(imageFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Изображение успешно загружено");
            response.put("imageUrl", imageUrl);
            response.put("fileName", imageFile.getOriginalFilename());
            response.put("fileSize", imageFile.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при загрузке изображения: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Удаление изображения
    @DeleteMapping("/upload/image")
    public ResponseEntity<?> deleteImage(@RequestParam("imageUrl") String imageUrl, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется авторизация");
        }

        try {
            // Проверяем, что это локальный файл
            if (!imageUrl.startsWith("/uploads/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Можно удалять только загруженные файлы"));
            }

            boolean success = fileStorageService.deleteFile(imageUrl);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Изображение успешно удалено" : "Не удалось удалить изображение");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при удалении изображения: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    // === API для пользователей ===

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            HttpSession session) {

        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Требуется авторизация"));
        }

        try {
            // Вычисляем offset для пагинации
            int offset = (page - 1) * perPage;

            List<User> users;
            int totalUsers;

            if (search != null && !search.trim().isEmpty()) {
                users = userDAO.searchUsers(search, role, status, offset, perPage);
                totalUsers = userDAO.countSearchedUsers(search, role, status);
            } else {
                users = userDAO.getUsersWithFilters(role, status, offset, perPage);
                totalUsers = userDAO.countUsersWithFilters(role, status);
            }

            // Преобразуем в DTO
            List<Map<String, Object>> usersDTO = users.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("email", user.getEmail());
                userMap.put("role", user.getRole());
                userMap.put("active", user.isActive());
                userMap.put("email_verified", user.isEmailVerified());
                userMap.put("created_at", user.getCreatedAt());
                userMap.put("updated_at", user.getUpdatedAt());
                return userMap;
            }).collect(Collectors.toList());

            int totalPages = (int) Math.ceil((double) totalUsers / perPage);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", usersDTO);

            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("per_page", perPage);
            pagination.put("total", totalUsers);
            pagination.put("total_pages", totalPages);
            pagination.put("has_next", page < totalPages);
            pagination.put("has_prev", page > 1);

            response.put("pagination", pagination);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при получении пользователей: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Требуется авторизация"));
        }

        try {
            User user = userDAO.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Пользователь не найден"));
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());
            userData.put("active", user.isActive());
            userData.put("email_verified", user.isEmailVerified());
            userData.put("created_at", user.getCreatedAt());
            userData.put("updated_at", user.getUpdatedAt());

            return ResponseEntity.ok(Map.of("success", true, "user", userData));

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при получении пользователя: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> statusData,
            HttpSession session) {

        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Требуется авторизация"));
        }

        try {
            User user = userDAO.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Пользователь не найден"));
            }

            // Защита от изменения администратора
            if ("admin".equals(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Нельзя изменять статус администратора системы"));
            }

            // Проверяем, не пытаемся ли изменить статус самого себя
            Object currentUserId = session.getAttribute("userId");
            if (currentUserId != null && user.getId().equals(Long.parseLong(currentUserId.toString()))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Нельзя изменить статус своему собственному аккаунту"));
            }

            Boolean active = (Boolean) statusData.get("active");
            if (active == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Не указан статус"));
            }

            boolean success = userDAO.updateUserStatus(id, active);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ?
                    (active ? "Пользователь успешно активирован" : "Пользователь успешно заблокирован") :
                    "Не удалось обновить статус пользователя");
            response.put("active", active);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при обновлении статуса пользователя: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Требуется авторизация"));
        }

        try {
            User user = userDAO.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Пользователь не найден"));
            }

            // Защита от удаления администратора
            if ("admin".equals(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Нельзя удалить администратора системы"));
            }

            // Проверяем, не пытаемся ли удалить самого себя
            Object currentUserId = session.getAttribute("userId");
            if (currentUserId != null && user.getId().equals(Long.parseLong(currentUserId.toString()))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Нельзя удалить свой собственный аккаунт"));
            }

            boolean success = userDAO.deleteUser(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ?
                    "Пользователь успешно удален" :
                    "Не удалось удалить пользователя");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при удалении пользователя: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
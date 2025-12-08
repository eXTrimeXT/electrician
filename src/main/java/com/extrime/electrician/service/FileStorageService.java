package com.extrime.electrician.service;

import com.extrime.electrician.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Autowired
    private Config config;

     // Сохраняет загруженный файл и возвращает URL для доступа к нему
    public String storeFile(MultipartFile file) throws IOException {
        // Создаем директорию, если она не существует
        Path uploadPath = Paths.get(config.getUploadDir()).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // Генерируем уникальное имя файла
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // Сохраняем файл
        Path targetLocation = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Возвращаем URL для доступа к файлу
        return "/static/uploads/" + fileName;
    }

     // Удаляет файл по URL
    public boolean deleteFile(String fileUrl) {
        try {
            // Извлекаем имя файла из URL
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(config.getUploadDir()).resolve(fileName).normalize();

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при удалении файла: " + e.getMessage());
            return false;
        }
    }

     // Проверяет, является ли файл изображением
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    // Получает расширение файла
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

     // Возвращает максимальный размер файла (в байтах)
    public long getMaxFileSize() {
        return 10 * 1024 * 1024; // 10MB
    }

     // Возвращает разрешенные расширения файлов
    public String[] getAllowedExtensions() {
        return new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
    }
}
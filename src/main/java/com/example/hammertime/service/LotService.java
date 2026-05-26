package com.example.hammertime.service;

import com.example.hammertime.dto.LotForm;
import com.example.hammertime.model.Lot;
import com.example.hammertime.model.LotStatus;
import com.example.hammertime.model.User;
import com.example.hammertime.repository.LotRepository;
import com.example.hammertime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LotService {

    private final LotRepository lotRepository;
    private final UserRepository userRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public LotService(LotRepository lotRepository,
                      UserRepository userRepository) {
        this.lotRepository = lotRepository;
        this.userRepository = userRepository;
    }

    public void createLot(LotForm form, String ownerEmail) {
        if (!form.getEndTime().isAfter(form.getStartTime())) {
            throw new IllegalArgumentException("Время окончания должно быть позже времени начала");
        }

        if (form.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Время окончания не может быть в прошлом");
        }

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Lot lot = new Lot();
        lot.setTitle(form.getTitle());
        lot.setDescription(form.getDescription());
        lot.setStartPrice(form.getStartPrice());
        lot.setCurrentPrice(form.getStartPrice());
        lot.setMinBid(form.getMinBid());
        lot.setStartTime(form.getStartTime());
        lot.setEndTime(form.getEndTime());
        lot.setOwnerId(owner.getId());

        if (form.getStartTime().isAfter(LocalDateTime.now())) {
            lot.setStatus(LotStatus.PLANNED);
        } else {
            lot.setStatus(LotStatus.ACTIVE);
        }

        Long lotId = lotRepository.save(lot);

        saveImages(lotId, form.getImages());
    }

    public List<Lot> findAll(String query, LocalDate date) {
        return lotRepository.findAll(query, date);
    }

    public Lot findById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Лот не найден"));
    }

    private void saveImages(Long lotId, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        try {
            Path uploadPath = Path.of(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile image : images) {
                if (image.isEmpty()) {
                    continue;
                }

                String originalName = StringUtils.cleanPath(image.getOriginalFilename());
                String extension = "";

                int dotIndex = originalName.lastIndexOf(".");
                if (dotIndex >= 0) {
                    extension = originalName.substring(dotIndex);
                }

                String fileName = UUID.randomUUID() + extension;
                Path filePath = uploadPath.resolve(fileName);

                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                lotRepository.saveImage(lotId, fileName);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при сохранении изображений");
        }
    }
}
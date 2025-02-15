package com.amcamp.infra.scheduler;

import com.amcamp.domain.image.application.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageCleanupScheduler {

    private final ImageService imageService;

    @Scheduled(cron = "00 00 00 * * *", zone = "Asia/Seoul")
    public void deleteAllImages() {
        imageService.deleteAllImage();
    }
}

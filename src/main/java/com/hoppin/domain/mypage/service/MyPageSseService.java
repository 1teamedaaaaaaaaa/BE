package com.hoppin.domain.mypage.service;

import com.hoppin.domain.mypage.dto.MyPagePromotionItemResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class MyPageSseService {

    private static final long SSE_TIMEOUT_MILLIS = 30L * 60L * 1000L;

    private final MyPageSseEmitterRepository emitterRepository;
    private final MyPageService myPageService;

    public SseEmitter subscribe(Long musicianId) {
        String emitterId = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);

        emitterRepository.save(musicianId, emitterId, emitter);
        emitter.onCompletion(() -> emitterRepository.remove(musicianId, emitterId));
        emitter.onTimeout(() -> emitterRepository.remove(musicianId, emitterId));
        emitter.onError(ignored -> emitterRepository.remove(musicianId, emitterId));

        sendToEmitter(musicianId, emitterId, emitter, "connected", Map.of("message", "mypage stream connected"));
        return emitter;
    }

    public void publishPromotionUpdatedAfterCommit(Long musicianId, Long promotionId) {
        Runnable publishTask = () -> publishPromotionUpdated(musicianId, promotionId);

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishTask.run();
                }
            });
            return;
        }

        publishTask.run();
    }

    public void publishPromotionUpdated(Long musicianId, Long promotionId) {
        MyPagePromotionItemResponse promotionItem = myPageService.getMyPromotionItem(musicianId, promotionId);

        emitterRepository.findAllByMusicianId(musicianId)
                .forEach((emitterId, emitter) ->
                        sendToEmitter(
                                musicianId,
                                emitterId,
                                emitter,
                                "promotion-analysis-updated",
                                promotionItem
                        )
                );
    }

    @Scheduled(fixedDelay = 25000)
    public void sendHeartbeat() {
        emitterRepository.findAll().forEach(musicianEmitters ->
                musicianEmitters.forEach((emitterId, emitter) ->
                        sendHeartbeatToEmitter(emitterId, emitter)
                )
        );
    }

    private void sendHeartbeatToEmitter(String emitterId, SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event()
                    .name("heartbeat")
                    .data("ping"));
        } catch (IOException | IllegalStateException exception) {
            removeEmitter(emitterId);
        }
    }

    private void sendToEmitter(
            Long musicianId,
            String emitterId,
            SseEmitter emitter,
            String eventName,
            Object data
    ) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (IOException | IllegalStateException exception) {
            emitterRepository.remove(musicianId, emitterId);
        }
    }

    private void removeEmitter(String emitterId) {
        emitterRepository.findAll().forEach(musicianEmitters ->
                musicianEmitters.entrySet().removeIf(entry -> entry.getKey().equals(emitterId))
        );
    }
}

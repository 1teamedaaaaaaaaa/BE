package com.hoppin.domain.mypage.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class MyPageSseEmitterRepository {

    private final Map<Long, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long musicianId, String emitterId, SseEmitter emitter) {
        emitters.computeIfAbsent(musicianId, ignored -> new ConcurrentHashMap<>())
                .put(emitterId, emitter);
        return emitter;
    }

    public void remove(Long musicianId, String emitterId) {
        Map<String, SseEmitter> musicianEmitters = emitters.get(musicianId);
        if (musicianEmitters == null) {
            return;
        }

        musicianEmitters.remove(emitterId);
        if (musicianEmitters.isEmpty()) {
            emitters.remove(musicianId);
        }
    }

    public Map<String, SseEmitter> findAllByMusicianId(Long musicianId) {
        return emitters.getOrDefault(musicianId, Map.of());
    }

    public Collection<Map<String, SseEmitter>> findAll() {
        return emitters.values();
    }
}

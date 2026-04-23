package com.hoppin.domain.musician.service;

import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.enumtype.MusicianStatus;
import com.hoppin.domain.musician.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawnMusicianCleanupService {

    private final MusicianRepository musicianRepository;

    @Transactional
    // 3개월 뒤 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanup() {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(3);

        List<Musician> targets =
                musicianRepository.findAllByStatusAndWithdrawnAtBefore(MusicianStatus.WITHDRAWN, threshold);

        if (targets.isEmpty()) {
            return;
        }

        log.info("3개월 경과 탈퇴 회원 삭제 수 = {}", targets.size());
        musicianRepository.deleteAll(targets);
    }
}
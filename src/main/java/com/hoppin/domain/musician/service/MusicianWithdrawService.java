package com.hoppin.domain.musician.service;

import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MusicianWithdrawService {

    private final MusicianRepository musicianRepository;

    public void withdraw(Long musicianId) {
        Musician musician = musicianRepository.findById(musicianId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        musician.withdraw();
    }
}
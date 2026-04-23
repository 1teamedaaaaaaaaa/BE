package com.hoppin.domain.musician.repository;

import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.enumtype.MusicianStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MusicianRepository {
    Musician save(Musician musician);
    Optional<Musician> findById(Long musicianId);
    Optional<Musician> findByEmail(String email);
    boolean existsById(Long musicianId);

    //회원 탈퇴
    List<Musician> findAllByStatusAndWithdrawnAtBefore(MusicianStatus status, LocalDateTime withdrawnAt);
    void deleteAll(List<Musician> musicians);
}

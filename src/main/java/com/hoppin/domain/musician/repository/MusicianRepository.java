package com.hoppin.domain.musician.repository;

import com.hoppin.domain.musician.entity.Musician;

import java.util.Optional;

public interface MusicianRepository {
    Musician save(Musician musician);
    Optional<Musician> findById(Long musicianId);
    boolean existsById(Long musicianId);
}
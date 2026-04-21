package com.hoppin.domain.Musician.repository;

import com.hoppin.domain.Musician.entity.Musician;

import java.util.Optional;

public interface MusicianRepository {

    Musician save(Musician musician);

    Optional<Musician> findById(Long musicianId);

    boolean existsById(Long musicianId);
}

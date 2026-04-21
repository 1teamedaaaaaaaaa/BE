package com.hoppin.domain.musician.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hoppin.domain.musician.entity.Musician;

import java.util.Optional;
@Repository
public interface MusicianRepository extends JpaRepository<Musician, Long> {
    Optional<Musician> findByEmail(String email);
}
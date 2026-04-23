package com.hoppin.domain.musician.infrastructure;

import com.hoppin.domain.musician.entity.Musician;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusicianJpaRepository extends JpaRepository<Musician, Long> {
    Optional<Musician> findByEmail(String email);
}

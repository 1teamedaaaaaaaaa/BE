package com.hoppin.domain.musician.infrastructure;

import com.hoppin.domain.musician.entity.Musician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicianJpaRepository extends JpaRepository<Musician, Long> {
}
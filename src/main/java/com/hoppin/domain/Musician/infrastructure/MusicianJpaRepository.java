package com.hoppin.domain.Musician.infrastructure;

import com.hoppin.domain.Musician.entity.Musician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicianJpaRepository extends JpaRepository<Musician, Long> {
}

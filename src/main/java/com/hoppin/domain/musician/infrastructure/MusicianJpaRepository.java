package com.hoppin.domain.musician.infrastructure;

import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.enumtype.MusicianStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MusicianJpaRepository extends JpaRepository<Musician, Long> {
    Optional<Musician> findByEmail(String email);
    
    List<Musician> findAllByStatusAndWithdrawnAtBefore(MusicianStatus status, LocalDateTime withdrawnAt);
}

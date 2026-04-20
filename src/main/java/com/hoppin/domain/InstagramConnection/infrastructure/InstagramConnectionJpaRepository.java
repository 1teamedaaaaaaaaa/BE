package com.hoppin.domain.InstagramConnection.infrastructure;

import com.hoppin.domain.InstagramConnection.entity.InstagramConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstagramConnectionJpaRepository extends JpaRepository<InstagramConnection, Long> {

    Optional<InstagramConnection> findByMusicianId(Long musicianId);

    Optional<InstagramConnection> findByInstagramAccountId(String instagramAccountId);
}

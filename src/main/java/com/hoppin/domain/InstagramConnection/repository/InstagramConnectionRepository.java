package com.hoppin.domain.InstagramConnection.repository;

import com.hoppin.domain.InstagramConnection.entity.InstagramConnection;

import java.util.Optional;

public interface InstagramConnectionRepository {

    InstagramConnection save(InstagramConnection instagramConnection);

    Optional<InstagramConnection> findByMusicianId(Long musicianId);

    Optional<InstagramConnection> findByInstagramAccountId(String instagramAccountId);
}

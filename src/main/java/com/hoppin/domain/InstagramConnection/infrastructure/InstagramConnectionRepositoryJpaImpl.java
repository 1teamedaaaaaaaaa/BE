package com.hoppin.domain.InstagramConnection.infrastructure;

import com.hoppin.domain.InstagramConnection.entity.InstagramConnection;
import com.hoppin.domain.InstagramConnection.repository.InstagramConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InstagramConnectionRepositoryJpaImpl implements InstagramConnectionRepository {

    private final InstagramConnectionJpaRepository instagramConnectionJpaRepository;

    @Override
    public InstagramConnection save(InstagramConnection instagramConnection) {
        return instagramConnectionJpaRepository.save(instagramConnection);
    }

    @Override
    public Optional<InstagramConnection> findByMusicianId(Long musicianId) {
        return instagramConnectionJpaRepository.findByMusicianId(musicianId);
    }

    @Override
    public Optional<InstagramConnection> findByInstagramAccountId(String instagramAccountId) {
        return instagramConnectionJpaRepository.findByInstagramAccountId(instagramAccountId);
    }
}

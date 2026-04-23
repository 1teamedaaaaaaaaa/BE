package com.hoppin.domain.musician.infrastructure;

import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.enumtype.MusicianStatus;
import com.hoppin.domain.musician.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MusicianRepositoryJpaImpl implements MusicianRepository {

    private final MusicianJpaRepository musicianJpaRepository;

    @Override
    public Musician save(Musician musician) {
        return musicianJpaRepository.save(musician);
    }

    @Override
    public Optional<Musician> findById(Long musicianId) {
        return musicianJpaRepository.findById(musicianId);
    }

    @Override
    public Optional<Musician> findByEmail(String email) {
        return musicianJpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsById(Long musicianId) {
        return musicianJpaRepository.existsById(musicianId);
    }

    @Override
    public List<Musician> findAllByStatusAndWithdrawnAtBefore(MusicianStatus status, LocalDateTime withdrawnAt) {
        return musicianJpaRepository.findAllByStatusAndWithdrawnAtBefore(status, withdrawnAt);
    }

    @Override
    public void deleteAll(List<Musician> musicians) {
        musicianJpaRepository.deleteAll(musicians);
    }
}

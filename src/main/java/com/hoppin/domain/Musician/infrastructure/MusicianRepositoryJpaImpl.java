package com.hoppin.domain.Musician.infrastructure;

import com.hoppin.domain.Musician.entity.Musician;
import com.hoppin.domain.Musician.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public boolean existsById(Long musicianId) {
        return musicianJpaRepository.existsById(musicianId);
    }
}

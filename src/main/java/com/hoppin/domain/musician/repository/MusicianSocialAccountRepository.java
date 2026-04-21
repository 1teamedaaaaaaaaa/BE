package com.hoppin.domain.musician.repository;

import com.hoppin.domain.musician.entity.MusicianSocialAccount;
import com.hoppin.domain.musician.enumtype.AuthProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicianSocialAccountRepository extends JpaRepository<MusicianSocialAccount, Long> {

    Optional<MusicianSocialAccount> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}
package com.hoppin.domain.member.repository;

import com.hoppin.domain.member.entity.AuthProvider;
import com.hoppin.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
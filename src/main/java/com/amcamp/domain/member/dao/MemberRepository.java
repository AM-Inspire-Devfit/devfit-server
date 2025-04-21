package com.amcamp.domain.member.dao;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByOauthInfo(OauthInfo oauthInfo);
}

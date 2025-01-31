package com.amcamp.domain.member.dao;

import com.amcamp.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}

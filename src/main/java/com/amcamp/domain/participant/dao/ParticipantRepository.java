package com.amcamp.domain.participant.dao;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.participant.domain.Participant;
import com.amcamp.domain.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
	Optional<Participant> findByMemberAndTeam(Member member, Team team);
}

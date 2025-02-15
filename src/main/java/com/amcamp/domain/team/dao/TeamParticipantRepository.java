package com.amcamp.domain.team.dao;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamParticipantRepository
        extends JpaRepository<TeamParticipant, Long>, TeamParticipantRepositoryCustom {
    Optional<TeamParticipant> findByMemberAndTeam(Member member, Team team);

    void deleteByTeam(Team team);

    Optional<TeamParticipant> findByTeamId(Long teamId);
}

package com.amcamp.domain.team.dao;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.dto.response.BasicMemberResponse;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.domain.team.domain.TeamParticipantRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamParticipantRepository
        extends JpaRepository<TeamParticipant, Long>, TeamParticipantRepositoryCustom {
    Optional<TeamParticipant> findByMemberAndTeam(Member member, Team team);

    void deleteByTeam(Team team);

    Optional<TeamParticipant> findByTeamId(Long teamId);

    //    @Query("SELECT m.id, m.nickname, m.profileImageUrl "
    //                            + "FROM Member m "
    //                            + "JOIN TeamParticipant tp ON m.id = tp.member.id "
    //                            + "WHERE tp.team.id = :teamId "
    //                            + "AND tp.role = :role")
    //    BasicMemberResponse findAdmin(Long teamId, TeamParticipantRole role);

    @Query(
            "SELECT new com.amcamp.domain.member.dto.response.BasicMemberResponse(m.id, m.nickname, m.profileImageUrl) "
                    + "FROM Member m "
                    + "JOIN TeamParticipant tp ON m.id = tp.member.id "
                    + "WHERE tp.team.id = :teamId "
                    + "AND tp.role = :role ")
    BasicMemberResponse findAdmin(
            @Param("teamId") Long teamId, @Param("role") TeamParticipantRole role);
}

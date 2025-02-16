package com.amcamp.domain.team.dao;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.domain.team.domain.TeamParticipantRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamParticipantRepository extends JpaRepository<TeamParticipant, Long> {
    Optional<TeamParticipant> findByMemberAndTeam(Member member, Team team);

    void deleteByTeam(Team team);

    Optional<TeamParticipant> findByTeamId(Long teamId);

    //    @Query(
    //            "SELECT new com.amcamp.domain.team.dto.response.TeamAdminResponse (m.id,
    // m.nickname, m.profileImageUrl) "
    //                    + "FROM Member m "
    //                    + "JOIN TeamParticipant tp ON m.id = tp.member.id "
    //                    + "WHERE tp.team.id = :teamId "
    //                    + "AND tp.role = :role ")
    //    TeamAdminResponse findAdmin(
    //            @Param("teamId") Long teamId, @Param("role") TeamParticipantRole role);

    @Query(
            "SELECT m.id as id, m.nickname as nickname, m.profileImageUrl as profileImageUrl "
                    + "FROM Member m "
                    + "JOIN TeamParticipant tp ON m.id = tp.member.id "
                    + "WHERE tp.team.id = :teamId "
                    + "AND tp.role = :role")
    TeamAdminProjection findAdmin(
            @Param("teamId") Long teamId, @Param("role") TeamParticipantRole role);
}

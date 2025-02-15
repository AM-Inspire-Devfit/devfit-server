package com.amcamp.domain.team.dao;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.dto.response.BasicMemberResponse;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import java.util.Optional;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamParticipantRepository
        extends JpaRepository<TeamParticipant, Long>, TeamParticipantRepositoryCustom {
    Optional<TeamParticipant> findByMemberAndTeam(Member member, Team team);

    void deleteByTeam(Team team);

    Optional<TeamParticipant> findByTeamId(Long teamId);

    @Query(
            value =
                    "SELECT m.id, m.nickname, m.profileImageUrl "
                            + "FROM member m "
                            + "JOIN team_participant tp ON m.id = tp.member_id "
                            + "WHERE tp.team_id = :teamId AND m.id != :memberId "
                            + "ORDER BY tp.created_dt DESC "
                            + "LIMIT :pageSize",
            nativeQuery = true)
    Slice<BasicMemberResponse> findMemberByTeamExceptMember(
            Long teamId, Long memberId, int pageSize);
}

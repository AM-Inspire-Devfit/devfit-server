package com.amcamp.domain.team.dao;

import static com.amcamp.domain.member.domain.QMember.member;
import static com.amcamp.domain.team.domain.QTeam.team;
import static com.amcamp.domain.team.domain.QTeamParticipant.teamParticipant;

import com.amcamp.domain.member.dto.response.BasicMemberResponse;
import com.amcamp.domain.team.domain.TeamParticipantRole;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamParticipantRepositoryImpl implements TeamParticipantRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<BasicMemberResponse> findMemberByTeamExceptAdmin(
            Long teamId, Long memberId, int pageSize) {
        List<BasicMemberResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        BasicMemberResponse.class,
                                        member.id,
                                        member.nickname,
                                        member.profileImageUrl))
                        .from(teamParticipant)
                        .leftJoin(teamParticipant.member, member)
                        .on(member.id.eq(teamParticipant.member.id))
                        .where(
                                team.id.eq(teamId),
                                member.id.ne(memberId),
                                teamParticipant.role.eq(TeamParticipantRole.valueOf("TEAM_USER")))
                        .orderBy(teamParticipant.createdDt.desc())
                        .limit(pageSize + 1)
                        .fetch();

        PageRequest pageRequest = PageRequest.of(0, pageSize);
        boolean hasNext = results.size() > pageSize;
        return new SliceImpl<>(
                results.subList(0, Math.min(pageSize, results.size())), pageRequest, hasNext);
    }
}

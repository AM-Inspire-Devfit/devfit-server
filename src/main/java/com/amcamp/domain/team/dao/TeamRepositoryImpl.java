package com.amcamp.domain.team.dao;

import static com.amcamp.domain.participant.domain.QParticipant.participant;
import static com.amcamp.domain.team.domain.QTeam.team;

import com.amcamp.domain.team.dto.response.TeamInfoResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<TeamInfoResponse> findAllTeamByMemberId(
            Long memberId, Long lastTeamId, int pageSize) {
        List<TeamInfoResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        TeamInfoResponse.class,
                                        team.id,
                                        team.name,
                                        team.description,
                                        team.emoji))
                        .from(participant)
                        .leftJoin(participant.team, team)
                        .on(team.id.eq(participant.team.id))
                        .where(lastTeamId(lastTeamId), participant.member.id.eq(memberId))
                        .orderBy(participant.createdDt.desc())
                        .limit(pageSize + 1)
                        .fetch();

        if (results.isEmpty()) {
            throw new CommonException(TeamErrorCode.TEAM_NOT_EXISTS);
        }

        return checkLastPage(pageSize, results);
    }

    private BooleanExpression lastTeamId(Long teamId) {
        if (teamId == null) {
            return null;
        }

        return team.id.lt(teamId);
    }

    private Slice<TeamInfoResponse> checkLastPage(int pageSize, List<TeamInfoResponse> results) {
        boolean hasNext = false;

        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }
}

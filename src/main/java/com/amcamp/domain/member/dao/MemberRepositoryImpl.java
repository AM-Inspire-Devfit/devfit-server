package com.amcamp.domain.member.dao;

import static com.amcamp.domain.member.domain.QMember.member;
import static com.amcamp.domain.participant.domain.QParticipant.participant;
import static com.amcamp.domain.team.domain.QTeam.team;

import com.amcamp.domain.member.dto.response.SelectedMemberResponse;
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
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<SelectedMemberResponse> findMemberByTeamExceptMember(
            Long teamId, Long memberId, int pageSize) {
        List<SelectedMemberResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        SelectedMemberResponse.class,
                                        member.id,
                                        member.nickname,
                                        member.profileImageUrl))
                        .from(participant)
                        .leftJoin(participant.member, member)
                        .on(member.id.eq(participant.member.id))
                        .where(team.id.eq(teamId), member.id.ne(memberId))
                        .orderBy(participant.createdDt.desc())
                        .limit(pageSize + 1)
                        .fetch();

        PageRequest pageRequest = PageRequest.of(0, pageSize);
        boolean hasNext = results.size() > pageSize;
        return new SliceImpl<>(
                results.subList(0, Math.min(pageSize, results.size())), pageRequest, hasNext);
    }
}

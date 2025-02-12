package com.amcamp.domain.team.application;

import static com.amcamp.global.common.constants.RedisConstants.*;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.participant.dao.ParticipantRepository;
import com.amcamp.domain.participant.domain.Participant;
import com.amcamp.domain.participant.domain.ParticipantRole;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.dto.request.TeamUpdateRequest;
import com.amcamp.domain.team.dto.response.TeamCheckResponse;
import com.amcamp.domain.team.dto.response.TeamInfoResponse;
import com.amcamp.domain.team.dto.response.TeamInviteCodeResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.util.MemberUtil;
import com.amcamp.global.util.RandomUtil;
import com.amcamp.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final MemberUtil memberUtil;
    private final TeamRepository teamRepository;
    private final ParticipantRepository participantRepository;
    private final RedisUtil redisUtil;
    private final RandomUtil randomUtil;

    public TeamInviteCodeResponse createTeam(String teamName, String teamDescription) {
        Member member = memberUtil.getCurrentMember();
        Team team = Team.createTeam(normalizeTeamName(teamName), teamDescription);
        teamRepository.save(team);

        Participant participant =
                Participant.createParticipant(member, team, ParticipantRole.ADMIN);
        participantRepository.save(participant);

        String code = randomUtil.generateCode(team.getId());

        return new TeamInviteCodeResponse(code);
    }

    public TeamInviteCodeResponse getInviteCode(Long teamId) {
        Member member = memberUtil.getCurrentMember();
        Team team = validateTeam(teamId);
        validateAdminParticipant(member, team);

        String code = randomUtil.generateCode(team.getId());

        return new TeamInviteCodeResponse(code);
    }

    public TeamCheckResponse getTeamByCode(String inviteCode) {
        Team team = searchTeamByCode(inviteCode);
        return new TeamCheckResponse(team.getId(), team.getTeamName());
    }

    public void joinTeam(String inviteCode) {
        Member member = memberUtil.getCurrentMember();
        Team team = searchTeamByCode(inviteCode);
        validateTeamJoin(member, team);

        Participant participant = Participant.createParticipant(member, team, ParticipantRole.USER);
        participantRepository.save(participant);
    }

    public TeamInfoResponse editTeam(Long teamId, TeamUpdateRequest teamUpdateRequest) {
        Member member = memberUtil.getCurrentMember();
        Team team = validateTeam(teamId);
        validateAdminParticipant(member, team);

        TeamUpdateRequest normalizedTeamUpdateRequest =
                new TeamUpdateRequest(
                        normalizeTeamName(teamUpdateRequest.teamName()),
                        teamUpdateRequest.teamDescription());

        team.updateTeam(normalizedTeamUpdateRequest);

        return new TeamInfoResponse(team.getId(), team.getTeamName(), team.getTeamDescription());
    }

    public void deleteTeam(Long teamId) {
        Member member = memberUtil.getCurrentMember();
        Team team = validateTeam(teamId);
        validateAdminParticipant(member, team);

        participantRepository.deleteByTeam(team);

        redisUtil
                .getData(TEAM_ID_PREFIX.formatted(teamId))
                .ifPresent(
                        inviteCode -> {
                            redisUtil.deleteData(INVITE_CODE_PREFIX.formatted(inviteCode));
                            redisUtil.deleteData(TEAM_ID_PREFIX.formatted(teamId));
                        });

        teamRepository.delete(team);
    }

    @Transactional(readOnly = true)
    public TeamInfoResponse getTeamInfo(Long teamId) {
        Member member = memberUtil.getCurrentMember();
        Team team = validateTeam(teamId);
        validateParticipant(member, team);

        return new TeamInfoResponse(team.getId(), team.getTeamName(), team.getTeamDescription());
    }

    private Team searchTeamByCode(String inviteCode) {
        Long teamId =
                redisUtil
                        .getData(INVITE_CODE_PREFIX.formatted(inviteCode))
                        .map(Long::valueOf)
                        .orElseThrow(() -> new CommonException(TeamErrorCode.INVALID_INVITE_CODE));

        return teamRepository
                .findById(teamId)
                .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));
    }

    private String normalizeTeamName(String name) {
        return name.replaceAll("[^0-9a-zA-Z가-힣 ]", "_");
    }

    private Team validateTeam(Long teamId) {
        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));
        return team;
    }

    private void validateTeamJoin(Member member, Team team) {
        if (participantRepository.findByMemberAndTeam(member, team).isPresent()) {
            throw new CommonException(TeamErrorCode.MEMBER_ALREADY_JOINED);
        }
    }

    private void validateParticipant(Member member, Team team) {
        if (!participantRepository.findByMemberAndTeam(member, team).isPresent()) {
            throw new CommonException(TeamErrorCode.TEAM_PARTICIPANT_NOT_FOUND);
        }
    }

    private void validateAdminParticipant(Member member, Team team) {
        Participant participant =
                participantRepository
                        .findByMemberAndTeam(member, team)
                        .orElseThrow(
                                () ->
                                        new CommonException(
                                                TeamErrorCode.TEAM_PARTICIPANT_NOT_FOUND));

        if (participant.getRole() != ParticipantRole.ADMIN) {
            throw new CommonException(TeamErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}

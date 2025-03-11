package com.amcamp.domain.rank.api;

import com.amcamp.domain.rank.application.RankService;
import com.amcamp.domain.rank.dto.response.BasicRankInfoResponse;
import com.amcamp.domain.rank.dto.response.RankInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "8. 랭크 API", description = "랭크 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ranks")
public class RankController {
    private final RankService rankService;

    @Operation(summary = "개별 회원 기여도 조회", description = "스프린트별 개별 회원의 기여도를 조회합니다.")
    @GetMapping("/{sprintId}/me")
    public BasicRankInfoResponse rankByMember(@PathVariable Long sprintId) {
        return rankService.getRankByMember(sprintId);
    }

    @Operation(summary = "프로젝트 내 회원 기여도 조회", description = "프로젝트 페이지에서 스프린트별 기여도를 조회합니다.")
    @GetMapping("/{sprintId}")
    public List<RankInfoResponse> rankBySprint(@PathVariable Long sprintId) {
        return rankService.getRankBySprint(sprintId);
    }
}

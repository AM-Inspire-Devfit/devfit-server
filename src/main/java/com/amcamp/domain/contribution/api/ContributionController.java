package com.amcamp.domain.contribution.api;

import com.amcamp.domain.contribution.application.ContributionService;
import com.amcamp.domain.contribution.dto.response.BasicContributionInfoResponse;
import com.amcamp.domain.contribution.dto.response.ContributionInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "8. 기여도 API", description = "기여도 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/contributions")
public class ContributionController {
    private final ContributionService contributionService;

    @Operation(summary = "개별 회원 기여도 조회", description = "스프린트별 개별 회원의 기여도를 조회합니다.")
    @GetMapping("/{projectId}")
    public BasicContributionInfoResponse contributionByMember(@PathVariable Long projectId) {
        return contributionService.getContributionByMember(projectId);
    }

    @Operation(summary = "프로젝트 내 회원 기여도 조회", description = "프로젝트 페이지에서 스프린트별 기여도를 조회합니다.")
    @GetMapping("/{sprintId}")
    public List<ContributionInfoResponse> contributionBySprint(@PathVariable Long sprintId) {
        return contributionService.getContributionBySprint(sprintId);
    }
}

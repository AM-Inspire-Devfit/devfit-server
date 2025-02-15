package com.amcamp.domain.project.dao;

import static com.amcamp.domain.project.domain.QProject.project;

import com.amcamp.domain.project.domain.Project;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Project> findAllByTeamId(Long teamId) {
        return queryFactory.selectFrom(project).where(project.team.id.eq(teamId)).fetch();
    }
}

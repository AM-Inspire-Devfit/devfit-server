package com.amcamp.domain.team.dao;

import com.amcamp.domain.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {}

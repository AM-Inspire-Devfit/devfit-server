package com.amcamp.domain.auth.dao;

import com.amcamp.domain.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}

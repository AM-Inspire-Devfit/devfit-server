package com.amcamp.domain.feedback.dao;

import com.amcamp.domain.feedback.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {}

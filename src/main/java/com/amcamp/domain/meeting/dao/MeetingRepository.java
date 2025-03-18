package com.amcamp.domain.meeting.dao;

import com.amcamp.domain.meeting.domain.Meeting;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Optional<Meeting> findMeetingById(Long id);

    Optional<Meeting> findMeetingByTitleAndMeetingDt(String title, LocalDateTime meetingDt);
}

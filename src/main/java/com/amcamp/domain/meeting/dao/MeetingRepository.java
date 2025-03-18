package com.amcamp.domain.meeting.dao;

import com.amcamp.domain.meeting.domain.Meeting;
<<<<<<< HEAD
import com.amcamp.domain.sprint.domain.Sprint;
import feign.Param;
=======
>>>>>>> 345eff9 (refactor: 동일 제목/타이틀 방지 검증 로직 추가)
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query(
            "select m from Meeting m where m.sprint = :sprint "
                    + "and ((:meetingStart between m.meetingStart and m.meetingEnd) "
                    + "or (:meetingEnd between m.meetingStart and m.meetingEnd) "
                    + "or (m.meetingStart between :meetingStart and :meetingEnd) "
                    + "or (m.meetingEnd between :meetingStart and :meetingEnd))")
    Optional<Meeting> findOverlappingMeeting(
            @Param("sprint") Sprint sprint,
            @Param("meetingStart") LocalDateTime meetingStart,
            @Param("meetingEnd") LocalDateTime meetingEnd);
}

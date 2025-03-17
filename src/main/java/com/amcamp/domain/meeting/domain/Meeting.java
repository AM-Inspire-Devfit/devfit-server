package com.amcamp.domain.meeting.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.sprint.domain.Sprint;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Meeting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;

    @Column(name = "meeting_title")
    private String title;

    @Column(name = "meeting_date")
    private LocalDateTime meetingDt;

    @Column(name = "meeting_status")
    private MeetingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @Builder(access = AccessLevel.PRIVATE)
    private Meeting(String title, LocalDateTime meetingDt, Sprint sprint, MeetingStatus status) {
        this.title = title;
        this.meetingDt = meetingDt;
        this.sprint = sprint;
        this.status = status;
    }

    public static Meeting createMeeting(String title, LocalDateTime meetingDt, Sprint sprint) {
        return Meeting.builder()
                .title(title)
                .meetingDt(meetingDt)
                .sprint(sprint)
                .status(MeetingStatus.OPEN)
                .build();
    }
}

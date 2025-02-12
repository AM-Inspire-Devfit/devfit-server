package com.amcamp.domain.team.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.team.dto.request.TeamEmojiUpdateRequest;
import com.amcamp.domain.team.dto.request.TeamUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    private String name;
    private String description;
    private String emoji;

    @Builder(access = AccessLevel.PRIVATE)
    private Team(String name, String description, String emoji) {
        this.name = name;
        this.description = description;
        this.emoji = emoji;
    }

    public static Team createTeam(String name, String description) {
        return Team.builder().name(name).description(description).emoji("🍇").build();
    }

    public void updateTeam(TeamUpdateRequest teamUpdateRequest) {
        this.name = teamUpdateRequest.teamName();
        this.description = teamUpdateRequest.teamDescription();
    }

    public void updateTeamEmoji(TeamEmojiUpdateRequest teamEmojiUpdateRequest) {
        this.emoji = teamEmojiUpdateRequest.teamEmoji();
    }
}

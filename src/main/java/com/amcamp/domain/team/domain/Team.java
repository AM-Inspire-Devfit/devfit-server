package com.amcamp.domain.team.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
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

    private String teamName;
    private String teamDescription;

    @Builder(access = AccessLevel.PRIVATE)
    private Team(String teamName, String teamDescription) {
        this.teamName = teamName;
        this.teamDescription = teamDescription;
    }

    public static Team createTeam(String teamName, String teamDescription) {
        return Team.builder().teamName(teamName).teamDescription(teamDescription).build();
    }
}

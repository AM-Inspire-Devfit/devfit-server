package com.amcamp.domain.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamParticipant is a Querydsl query type for TeamParticipant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamParticipant extends EntityPathBase<TeamParticipant> {

    private static final long serialVersionUID = 900148359L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamParticipant teamParticipant = new QTeamParticipant("teamParticipant");

    public final com.amcamp.domain.common.model.QBaseTimeEntity _super = new com.amcamp.domain.common.model.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDt = _super.createdDt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.amcamp.domain.member.domain.QMember member;

    public final EnumPath<TeamParticipantRole> role = createEnum("role", TeamParticipantRole.class);

    public final QTeam team;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDt = _super.updatedDt;

    public QTeamParticipant(String variable) {
        this(TeamParticipant.class, forVariable(variable), INITS);
    }

    public QTeamParticipant(Path<? extends TeamParticipant> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamParticipant(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamParticipant(PathMetadata metadata, PathInits inits) {
        this(TeamParticipant.class, metadata, inits);
    }

    public QTeamParticipant(Class<? extends TeamParticipant> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.amcamp.domain.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.team = inits.isInitialized("team") ? new QTeam(forProperty("team")) : null;
    }

}


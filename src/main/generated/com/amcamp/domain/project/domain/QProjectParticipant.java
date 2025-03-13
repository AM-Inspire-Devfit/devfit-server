package com.amcamp.domain.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectParticipant is a Querydsl query type for ProjectParticipant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectParticipant extends EntityPathBase<ProjectParticipant> {

    private static final long serialVersionUID = -307434407L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectParticipant projectParticipant = new QProjectParticipant("projectParticipant");

    public final com.amcamp.domain.common.model.QBaseTimeEntity _super = new com.amcamp.domain.common.model.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDt = _super.createdDt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProject project;

    public final StringPath projectNickname = createString("projectNickname");

    public final StringPath projectProfile = createString("projectProfile");

    public final EnumPath<ProjectParticipantRole> projectRole = createEnum("projectRole", ProjectParticipantRole.class);

    public final com.amcamp.domain.team.domain.QTeamParticipant teamParticipant;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDt = _super.updatedDt;

    public QProjectParticipant(String variable) {
        this(ProjectParticipant.class, forVariable(variable), INITS);
    }

    public QProjectParticipant(Path<? extends ProjectParticipant> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectParticipant(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectParticipant(PathMetadata metadata, PathInits inits) {
        this(ProjectParticipant.class, metadata, inits);
    }

    public QProjectParticipant(Class<? extends ProjectParticipant> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
        this.teamParticipant = inits.isInitialized("teamParticipant") ? new com.amcamp.domain.team.domain.QTeamParticipant(forProperty("teamParticipant"), inits.get("teamParticipant")) : null;
    }

}


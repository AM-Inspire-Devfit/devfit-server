package com.amcamp.domain.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectRegistration is a Querydsl query type for ProjectRegistration
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectRegistration extends EntityPathBase<ProjectRegistration> {

    private static final long serialVersionUID = -311127981L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectRegistration projectRegistration = new QProjectRegistration("projectRegistration");

    public final com.amcamp.domain.common.model.QBaseTimeEntity _super = new com.amcamp.domain.common.model.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDt = _super.createdDt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProject project;

    public final com.amcamp.domain.team.domain.QTeamParticipant requester;

    public final EnumPath<ProjectRegistrationStatus> requestStatus = createEnum("requestStatus", ProjectRegistrationStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDt = _super.updatedDt;

    public QProjectRegistration(String variable) {
        this(ProjectRegistration.class, forVariable(variable), INITS);
    }

    public QProjectRegistration(Path<? extends ProjectRegistration> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectRegistration(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectRegistration(PathMetadata metadata, PathInits inits) {
        this(ProjectRegistration.class, metadata, inits);
    }

    public QProjectRegistration(Class<? extends ProjectRegistration> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
        this.requester = inits.isInitialized("requester") ? new com.amcamp.domain.team.domain.QTeamParticipant(forProperty("requester"), inits.get("requester")) : null;
    }

}


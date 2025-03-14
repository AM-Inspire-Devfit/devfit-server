package com.amcamp.domain.contribution.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QContribution is a Querydsl query type for Contribution
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContribution extends EntityPathBase<Contribution> {

    private static final long serialVersionUID = -211222030L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContribution contribution = new QContribution("score");

    public final com.amcamp.domain.common.model.QBaseTimeEntity _super = new com.amcamp.domain.common.model.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDt = _super.createdDt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.amcamp.domain.project.domain.QProjectParticipant participant;

    public final NumberPath<Double> score = createNumber("score", Double.class);

    public final com.amcamp.domain.sprint.domain.QSprint sprint;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDt = _super.updatedDt;

    public QContribution(String variable) {
        this(Contribution.class, forVariable(variable), INITS);
    }

    public QContribution(Path<? extends Contribution> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QContribution(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QContribution(PathMetadata metadata, PathInits inits) {
        this(Contribution.class, metadata, inits);
    }

    public QContribution(Class<? extends Contribution> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.participant = inits.isInitialized("participant") ? new com.amcamp.domain.project.domain.QProjectParticipant(forProperty("participant"), inits.get("participant")) : null;
        this.sprint = inits.isInitialized("sprint") ? new com.amcamp.domain.sprint.domain.QSprint(forProperty("sprint"), inits.get("sprint")) : null;
    }

}


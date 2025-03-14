package com.amcamp.domain.contribution.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRank is a Querydsl query type for Contribution
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRank extends EntityPathBase<Contribution> {

    private static final long serialVersionUID = -309190614L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRank rank = new QRank("rank");

    public final com.amcamp.domain.common.model.QBaseTimeEntity _super = new com.amcamp.domain.common.model.QBaseTimeEntity(this);

    public final NumberPath<Double> contribution = createNumber("score", Double.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDt = _super.createdDt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.amcamp.domain.project.domain.QProjectParticipant participant;

    public final com.amcamp.domain.sprint.domain.QSprint sprint;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDt = _super.updatedDt;

    public QRank(String variable) {
        this(Contribution.class, forVariable(variable), INITS);
    }

    public QRank(Path<? extends Contribution> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRank(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRank(PathMetadata metadata, PathInits inits) {
        this(Contribution.class, metadata, inits);
    }

    public QRank(Class<? extends Contribution> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.participant = inits.isInitialized("participant") ? new com.amcamp.domain.project.domain.QProjectParticipant(forProperty("participant"), inits.get("participant")) : null;
        this.sprint = inits.isInitialized("sprint") ? new com.amcamp.domain.sprint.domain.QSprint(forProperty("sprint"), inits.get("sprint")) : null;
    }

}


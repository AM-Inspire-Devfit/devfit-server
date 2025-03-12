package com.amcamp.domain.sprint.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSprint is a Querydsl query type for Sprint
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSprint extends EntityPathBase<Sprint> {

    private static final long serialVersionUID = 1848039046L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSprint sprint = new QSprint("sprint");

    public final com.amcamp.domain.common.model.QBaseTimeEntity _super = new com.amcamp.domain.common.model.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDt = _super.createdDt;

    public final StringPath goal = createString("goal");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> progress = createNumber("progress", Double.class);

    public final com.amcamp.domain.project.domain.QProject project;

    public final ListPath<com.amcamp.domain.rank.domain.Rank, com.amcamp.domain.rank.domain.QRank> rank = this.<com.amcamp.domain.rank.domain.Rank, com.amcamp.domain.rank.domain.QRank>createList("rank", com.amcamp.domain.rank.domain.Rank.class, com.amcamp.domain.rank.domain.QRank.class, PathInits.DIRECT2);

    public final ListPath<com.amcamp.domain.task.domain.Task, com.amcamp.domain.task.domain.QTask> tasks = this.<com.amcamp.domain.task.domain.Task, com.amcamp.domain.task.domain.QTask>createList("tasks", com.amcamp.domain.task.domain.Task.class, com.amcamp.domain.task.domain.QTask.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final com.amcamp.domain.project.domain.QToDoInfo toDoInfo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDt = _super.updatedDt;

    public QSprint(String variable) {
        this(Sprint.class, forVariable(variable), INITS);
    }

    public QSprint(Path<? extends Sprint> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSprint(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSprint(PathMetadata metadata, PathInits inits) {
        this(Sprint.class, metadata, inits);
    }

    public QSprint(Class<? extends Sprint> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new com.amcamp.domain.project.domain.QProject(forProperty("project"), inits.get("project")) : null;
        this.toDoInfo = inits.isInitialized("toDoInfo") ? new com.amcamp.domain.project.domain.QToDoInfo(forProperty("toDoInfo")) : null;
    }

}


package com.amcamp.domain.task.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTask is a Querydsl query type for Task
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTask extends EntityPathBase<Task> {

    private static final long serialVersionUID = 436974940L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTask task = new QTask("task");

    public final com.amcamp.domain.common.model.QBaseTimeEntity _super = new com.amcamp.domain.common.model.QBaseTimeEntity(this);

    public final EnumPath<AssignedStatus> assignedStatus = createEnum("assignedStatus", AssignedStatus.class);

    public final com.amcamp.domain.project.domain.QProjectParticipant assignee;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDt = _super.createdDt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<SOSStatus> sosStatus = createEnum("sosStatus", SOSStatus.class);

    public final com.amcamp.domain.sprint.domain.QSprint sprint;

    public final EnumPath<TaskDifficulty> taskDifficulty = createEnum("taskDifficulty", TaskDifficulty.class);

    public final com.amcamp.domain.project.domain.QToDoInfo toDoInfo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDt = _super.updatedDt;

    public QTask(String variable) {
        this(Task.class, forVariable(variable), INITS);
    }

    public QTask(Path<? extends Task> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTask(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTask(PathMetadata metadata, PathInits inits) {
        this(Task.class, metadata, inits);
    }

    public QTask(Class<? extends Task> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.assignee = inits.isInitialized("assignee") ? new com.amcamp.domain.project.domain.QProjectParticipant(forProperty("assignee"), inits.get("assignee")) : null;
        this.sprint = inits.isInitialized("sprint") ? new com.amcamp.domain.sprint.domain.QSprint(forProperty("sprint"), inits.get("sprint")) : null;
        this.toDoInfo = inits.isInitialized("toDoInfo") ? new com.amcamp.domain.project.domain.QToDoInfo(forProperty("toDoInfo")) : null;
    }

}


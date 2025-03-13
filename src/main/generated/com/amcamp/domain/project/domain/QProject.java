package com.amcamp.domain.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = 448477818L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProject project = new QProject("project");

    public final com.amcamp.domain.common.model.QBaseTimeEntity _super = new com.amcamp.domain.common.model.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDt = _super.createdDt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.amcamp.domain.sprint.domain.Sprint, com.amcamp.domain.sprint.domain.QSprint> sprints = this.<com.amcamp.domain.sprint.domain.Sprint, com.amcamp.domain.sprint.domain.QSprint>createList("sprints", com.amcamp.domain.sprint.domain.Sprint.class, com.amcamp.domain.sprint.domain.QSprint.class, PathInits.DIRECT2);

    public final com.amcamp.domain.team.domain.QTeam team;

    public final StringPath title = createString("title");

    public final QToDoInfo toDoInfo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDt = _super.updatedDt;

    public QProject(String variable) {
        this(Project.class, forVariable(variable), INITS);
    }

    public QProject(Path<? extends Project> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProject(PathMetadata metadata, PathInits inits) {
        this(Project.class, metadata, inits);
    }

    public QProject(Class<? extends Project> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.team = inits.isInitialized("team") ? new com.amcamp.domain.team.domain.QTeam(forProperty("team")) : null;
        this.toDoInfo = inits.isInitialized("toDoInfo") ? new QToDoInfo(forProperty("toDoInfo")) : null;
    }

}


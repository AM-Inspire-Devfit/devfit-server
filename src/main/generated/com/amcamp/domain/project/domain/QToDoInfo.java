package com.amcamp.domain.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QToDoInfo is a Querydsl query type for ToDoInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QToDoInfo extends BeanPath<ToDoInfo> {

    private static final long serialVersionUID = -195586317L;

    public static final QToDoInfo toDoInfo = new QToDoInfo("toDoInfo");

    public final DatePath<java.time.LocalDate> dueDt = createDate("dueDt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> startDt = createDate("startDt", java.time.LocalDate.class);

    public final EnumPath<ToDoStatus> toDoStatus = createEnum("toDoStatus", ToDoStatus.class);

    public QToDoInfo(String variable) {
        super(ToDoInfo.class, forVariable(variable));
    }

    public QToDoInfo(Path<? extends ToDoInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QToDoInfo(PathMetadata metadata) {
        super(ToDoInfo.class, metadata);
    }

}


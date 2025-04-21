package com.amcamp.domain.image.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageFileExtension {
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg"),
    ;

    private final String extension;
}

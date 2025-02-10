package com.amcamp.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMsg {
    private String code;
    private String reason;

    @Override
    public String toString() {
        return String.format("ErrorMsg{code='%s', reason='%s'}", code, reason);
    }
}

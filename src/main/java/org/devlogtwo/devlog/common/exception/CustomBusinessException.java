package org.devlogtwo.devlog.common.exception;

import lombok.Getter;
import org.devlogtwo.devlog.common.code.ErrorCode;

@Getter
public class CustomBusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomBusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}

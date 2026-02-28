package com.whatshouldwedo.core.exception.type;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import lombok.Getter;

@Getter
public class ExternalServerException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String detailMessage;

    public ExternalServerException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    @Override
    public String getMessage() {
        return detailMessage;
    }
}

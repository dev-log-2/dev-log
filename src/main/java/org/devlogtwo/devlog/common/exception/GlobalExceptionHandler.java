package org.devlogtwo.devlog.common.exception;

import java.util.Objects;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomBusinessException 처리
    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleCustomBusinessException(CustomBusinessException e) {
        return ResponseHelper.error(e.getErrorCode());
    }

    // MethodArgumentNotValidException 처리 (@Valid 유효성 검사 실패)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        String customMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return ResponseHelper.error(ErrorCode.METHOD_ARGUMENT_NOT_VALID, customMessage);
    }

    // 정의되지 않은 내부 Exception 일괄 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleException(Exception e) {
        return ResponseHelper.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}

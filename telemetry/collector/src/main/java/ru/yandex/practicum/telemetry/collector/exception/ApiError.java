package ru.yandex.practicum.telemetry.collector.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ApiError {

    private String message;
    private String reason;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private List<String> errors;

    public ApiError(String message, String reason, String status) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(String message, String reason, String status, List<String> errors) {
        this(message, reason, status);
        this.errors = errors;
    }

    public static ApiError create(String message, String reason, String status) {
        return new ApiError(message, reason, status);
    }

    public static ApiError create(String message, String reason, String status, List<String> errors) {
        return new ApiError(message, reason, status, errors);
    }
}

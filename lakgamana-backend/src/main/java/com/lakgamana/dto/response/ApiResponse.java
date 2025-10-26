package com.lakgamana.dto.response;

 

import java.time.LocalDateTime;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String error;
    private LocalDateTime timestamp;

    public ApiResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(true);
        res.setMessage("Operation completed successfully");
        res.setData(data);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(true);
        res.setMessage(message);
        res.setData(data);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(false);
        res.setMessage(message);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }

    public static <T> ApiResponse<T> error(String message, String error) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(false);
        res.setMessage(message);
        res.setError(error);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }
}

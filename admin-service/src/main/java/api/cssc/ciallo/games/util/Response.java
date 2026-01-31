package api.cssc.ciallo.games.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response<T> {
    private int code;
    private String message;
    private T data;

    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> success() {
        return new Response<>(200, "success", null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data);
    }

    public static <T> Response<T> badRequest(String message) {
        return new Response<>(400, message, null);
    }

    public static <T> Response<T> notFound(String message) {
        return new Response<>(404, message, null);
    }

    public static <T> Response<T> unauthorized(String message) {
        return new Response<>(401, message, null);
    }

    public static <T> Response<T> internalError(String message) {
        return new Response<>(500, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
}

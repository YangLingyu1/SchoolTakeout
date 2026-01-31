package api.cssc.ciallo.games.util;

public class Response<T> {
    private Integer code;
    private String message;
    private T data;

    public Response(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    public static <T> Response<T> error(Integer code, String message) {
        return new Response<>(code, message, null);
    }

    public static <T> Response<T> badRequest(String message) {
        return new Response<>(400, message, null);
    }

    public static <T> Response<T> unauthorized(String message) {
        return new Response<>(401, message, null);
    }

    public static <T> Response<T> notFound(String message) {
        return new Response<>(404, message, null);
    }

    public static <T> Response<T> internalError(String message) {
        return new Response<>(500, message, null);
    }

    // Getters and Setters
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

package api.cssc.ciallo.games.entity;

public enum OrderStatus {
    PENDING_PAYMENT(0, "待支付"),
    PENDING_GRAB(1, "待接单"),
    DELIVERING(2, "配送中"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final Integer code;
    private final String description;

    OrderStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatus fromCode(Integer code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status code: " + code);
    }
}

package com.ywf.orderly;

public class OrderOperator {
    private Long orderId;
    private String operaName;

    public OrderOperator(Long orderId, String operaName) {
        this.orderId = orderId;
        this.operaName = operaName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOperaName() {
        return operaName;
    }

    public void setOperaName(String operaName) {
        this.operaName = operaName;
    }

    @Override
    public String toString() {
        return "OrderOperator{" +
                "orderId='" + orderId + '\'' +
                ", operaName='" + operaName + '\'' +
                '}';
    }
}

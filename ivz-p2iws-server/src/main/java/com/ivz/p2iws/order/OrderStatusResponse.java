package com.ivz.p2iws.order;

public class OrderStatusResponse {
  private final String orderId;
  private final OrderStatus status;

  public OrderStatusResponse(String orderId, OrderStatus status) {
    this.orderId = orderId;
    this.status = status;
  }

  public String getOrderId() {
    return orderId;
  }

  public OrderStatus getStatus() {
    return status;
  }
}
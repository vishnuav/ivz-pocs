package com.ivz.oms.order;

import com.ivz.common.model.order.OrderStatusResponse;

public interface OrderService {
  OrderStatusResponse getOrderStatus(String orderId);
}

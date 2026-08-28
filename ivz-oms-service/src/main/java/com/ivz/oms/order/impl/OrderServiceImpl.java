package com.ivz.oms.order.impl;

import com.ivz.common.model.latency.LatencyMonitor;
import com.ivz.common.model.order.OrderStatus;
import com.ivz.common.model.order.OrderStatusResponse;
import com.ivz.oms.order.OrderService;
import com.ivz.oms.service.OMSLoginLogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {
  private final OMSLoginLogoutService omsLoginLogoutService;

  @Autowired
  public OrderServiceImpl(OMSLoginLogoutService omsLoginLogoutService) {
    this.omsLoginLogoutService = omsLoginLogoutService;
  }

  @Override
  public OrderStatusResponse getOrderStatus(String orderId) {
    long requestId = LatencyMonitor.monitor(0L, "OrderServiceImpl.OrderServiceImpl", System.currentTimeMillis(), orderId);
    Objects.requireNonNull(omsLoginLogoutService.login(), "omsLoginLogoutService.login() must not return null");
    LatencyMonitor.monitor(requestId, "OrderServiceImpl.OrderServiceImpl", System.currentTimeMillis(), orderId);
    return new OrderStatusResponse(orderId, OrderStatus.Open);
  }
}
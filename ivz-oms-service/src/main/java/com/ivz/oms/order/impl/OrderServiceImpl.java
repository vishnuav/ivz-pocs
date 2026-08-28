package com.ivz.oms.order.impl;

import com.ivz.common.model.order.OrderStatus;
import com.ivz.common.model.order.OrderStatusResponse;
import com.ivz.oms.order.OrderService;
import com.ivz.oms.service.OMSLoginLogoutService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
  private final OMSLoginLogoutService omsLoginLogoutService;

  @Autowired
  public OrderServiceImpl(OMSLoginLogoutService omsLoginLogoutService) {
    this.omsLoginLogoutService = omsLoginLogoutService;
  }

  @Override
  public OrderStatusResponse getOrderStatus(String orderId) {
    Objects.requireNonNull(omsLoginLogoutService.login(), "omsLoginLogoutService.login() must not return null");
    return new OrderStatusResponse(orderId, OrderStatus.Open);
  }
}
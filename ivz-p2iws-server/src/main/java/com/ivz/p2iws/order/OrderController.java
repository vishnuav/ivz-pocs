package com.ivz.p2iws.order;

import com.ivz.common.model.order.OrderStatusResponse;
import com.ivz.oms.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
  private final OrderService orderService;

  @Autowired
  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("/api/orders/status")
  public ResponseEntity<OrderStatusResponse> getOrderStatus(@RequestParam("orderId") String orderId) {
    return ResponseEntity.ok(orderService.getOrderStatus(orderId));
  }
}
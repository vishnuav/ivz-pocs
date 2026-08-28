package com.ivz.p2iws.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
  @GetMapping("/api/orders/status")
  public ResponseEntity<OrderStatusResponse> getOrderStatus(@RequestParam("orderId") String orderId) {
    return ResponseEntity.ok(new OrderStatusResponse(orderId, OrderStatus.Open));
  }
}
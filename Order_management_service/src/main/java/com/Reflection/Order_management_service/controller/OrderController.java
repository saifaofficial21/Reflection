package com.Reflection.Order_management_service.controller;

import com.Reflection.Order_management_service.dto.CreateOrderRequest;
import com.Reflection.Order_management_service.dto.OrderResponse;
import com.Reflection.Order_management_service.dto.UpdateStatusRequest;
import com.Reflection.Order_management_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
		return ResponseEntity.ok(orderService.getOrderById(orderId));
	}

	@PutMapping("/{orderId}/status")
	public ResponseEntity<OrderResponse> updateStatus(@PathVariable String orderId,
			@Valid @RequestBody UpdateStatusRequest request) {
		return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request.getStatus()));
	}

	@GetMapping
	public ResponseEntity<List<OrderResponse>> listOrders() {
		return ResponseEntity.ok(orderService.listAllOrders());
	}
}

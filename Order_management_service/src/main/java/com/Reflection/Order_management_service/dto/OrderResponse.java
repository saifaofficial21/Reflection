package com.Reflection.Order_management_service.dto;

import com.Reflection.Order_management_service.model.Order;
import com.Reflection.Order_management_service.model.OrderStatus;

import java.time.Instant;

public class OrderResponse {

	private final String orderId;
	private final String customerName;
	private final Double amount;
	private final OrderStatus status;
	private final Instant createdAt;
	private final Instant updatedAt;

	private OrderResponse(Order order) {
		this.orderId = order.getOrderId();
		this.customerName = order.getCustomerName();
		this.amount = order.getAmount();
		this.status = order.getStatus();
		this.createdAt = order.getCreatedAt();
		this.updatedAt = order.getUpdatedAt();
	}

	public static OrderResponse from(Order order) {
		return new OrderResponse(order);
	}

	public String getOrderId() {
		return orderId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public Double getAmount() {
		return amount;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}

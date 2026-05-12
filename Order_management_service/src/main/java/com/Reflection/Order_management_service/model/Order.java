package com.Reflection.Order_management_service.model;

import java.time.Instant;

public class Order {

	private final String orderId;
	private final String customerName;
	private final Double amount;
	private OrderStatus status;
	private final Instant createdAt;
	private Instant updatedAt;

	public Order(String orderId, String customerName, Double amount) {
		this.orderId = orderId;
		this.customerName = customerName;
		this.amount = amount;
		this.status = OrderStatus.NEW;
		this.createdAt = Instant.now();
		this.updatedAt = Instant.now();
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

	public void setStatus(OrderStatus status) {
		this.status = status;
		this.updatedAt = Instant.now();
	}
}

package com.Reflection.Order_management_service.exception;

public class OrderNotFoundException extends RuntimeException {

	public OrderNotFoundException(String orderId) {
		super("Order not found: " + orderId);
	}
}

package com.Reflection.Order_management_service.exception;

import com.Reflection.Order_management_service.model.OrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {

	public InvalidStatusTransitionException(OrderStatus current, OrderStatus requested) {
		super(String.format("Cannot transition order from %s to %s", current, requested));
	}
}

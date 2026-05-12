package com.Reflection.Order_management_service.model;

public enum OrderStatus {
	NEW,
	PROCESSING,
	COMPLETED;

	public boolean canTransitionTo(OrderStatus next) {
		return switch (this) {
			case NEW -> next == PROCESSING;
			case PROCESSING -> next == COMPLETED;
			default -> false;
		};
	}
}

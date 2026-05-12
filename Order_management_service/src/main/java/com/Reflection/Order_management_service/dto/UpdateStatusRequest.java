package com.Reflection.Order_management_service.dto;

import com.Reflection.Order_management_service.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

	@NotNull(message = "status is mandatory")
	private OrderStatus status;

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}
}

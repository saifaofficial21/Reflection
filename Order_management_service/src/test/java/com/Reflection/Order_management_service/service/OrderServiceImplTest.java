package com.Reflection.Order_management_service.service;

import com.Reflection.Order_management_service.dto.CreateOrderRequest;
import com.Reflection.Order_management_service.dto.OrderResponse;
import com.Reflection.Order_management_service.exception.InvalidStatusTransitionException;
import com.Reflection.Order_management_service.exception.OrderNotFoundException;
import com.Reflection.Order_management_service.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderServiceImplTest {

	private OrderServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new OrderServiceImpl();
	}

	@Test
	@DisplayName("Creating an order should set status to NEW")
	void createOrder_setsStatusNew() {
		CreateOrderRequest req = buildRequest("Alice", 99.0);
		OrderResponse resp = service.createOrder(req);

		assertThat(resp.getOrderId()).isNotBlank();
		assertThat(resp.getCustomerName()).isEqualTo("Alice");
		assertThat(resp.getAmount()).isEqualTo(99.0);
		assertThat(resp.getStatus()).isEqualTo(OrderStatus.NEW);
	}

	@Test
	@DisplayName("Fetching an existing order returns the correct order")
	void getOrderById_existingOrder_returnsOrder() {
		OrderResponse created = service.createOrder(buildRequest("Bob", 150.0));
		OrderResponse fetched = service.getOrderById(created.getOrderId());

		assertThat(fetched.getOrderId()).isEqualTo(created.getOrderId());
	}

	@Test
	@DisplayName("Fetching a non-existent order throws OrderNotFoundException")
	void getOrderById_missingOrder_throws() {
		assertThatThrownBy(() -> service.getOrderById("does-not-exist")).isInstanceOf(OrderNotFoundException.class);
	}

	@Test
	@DisplayName("NEW -> PROCESSING is a valid transition")
	void updateStatus_newToProcessing_succeeds() {
		OrderResponse created = service.createOrder(buildRequest("Carol", 200.0));
		OrderResponse updated = service.updateOrderStatus(created.getOrderId(), OrderStatus.PROCESSING);

		assertThat(updated.getStatus()).isEqualTo(OrderStatus.PROCESSING);
	}

	@Test
	@DisplayName("PROCESSING -> COMPLETED is a valid transition")
	void updateStatus_processingToCompleted_succeeds() {
		OrderResponse created = service.createOrder(buildRequest("Dave", 300.0));
		service.updateOrderStatus(created.getOrderId(), OrderStatus.PROCESSING);
		OrderResponse updated = service.updateOrderStatus(created.getOrderId(), OrderStatus.COMPLETED);

		assertThat(updated.getStatus()).isEqualTo(OrderStatus.COMPLETED);
	}

	@Test
	@DisplayName("NEW -> COMPLETED is an invalid transition and throws")
	void updateStatus_newToCompleted_throws() {
		OrderResponse created = service.createOrder(buildRequest("Eve", 400.0));

		assertThatThrownBy(() -> service.updateOrderStatus(created.getOrderId(), OrderStatus.COMPLETED))
				.isInstanceOf(InvalidStatusTransitionException.class);
	}

	@Test
	@DisplayName("Updating a COMPLETED order to any status throws")
	void updateStatus_fromCompleted_throws() {
		OrderResponse created = service.createOrder(buildRequest("Frank", 500.0));
		service.updateOrderStatus(created.getOrderId(), OrderStatus.PROCESSING);
		service.updateOrderStatus(created.getOrderId(), OrderStatus.COMPLETED);

		assertThatThrownBy(() -> service.updateOrderStatus(created.getOrderId(), OrderStatus.PROCESSING))
				.isInstanceOf(InvalidStatusTransitionException.class);
	}

	@Test
	@DisplayName("Listing orders returns all created orders")
	void listAllOrders_returnsAll() {
		service.createOrder(buildRequest("G", 1.0));
		service.createOrder(buildRequest("H", 2.0));

		assertThat(service.listAllOrders()).hasSize(2);
	}

	private CreateOrderRequest buildRequest(String name, double amount) {
		CreateOrderRequest req = new CreateOrderRequest();
		req.setCustomerName(name);
		req.setAmount(amount);
		return req;
	}
}

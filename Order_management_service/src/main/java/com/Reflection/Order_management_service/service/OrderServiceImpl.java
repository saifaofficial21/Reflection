package com.Reflection.Order_management_service.service;

import com.Reflection.Order_management_service.dto.CreateOrderRequest;
import com.Reflection.Order_management_service.dto.OrderResponse;
import com.Reflection.Order_management_service.exception.InvalidStatusTransitionException;
import com.Reflection.Order_management_service.exception.OrderNotFoundException;
import com.Reflection.Order_management_service.model.Order;
import com.Reflection.Order_management_service.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

	private final Map<String, Order> store = new ConcurrentHashMap<>();

	@Override
	public OrderResponse createOrder(CreateOrderRequest request) {
		String orderId = UUID.randomUUID().toString();
		Order order = new Order(orderId, request.getCustomerName(), request.getAmount());
		store.put(orderId, order);
		return OrderResponse.from(order);
	}

	@Override
	public OrderResponse getOrderById(String orderId) {
		return OrderResponse.from(findOrThrow(orderId));
	}

	@Override
	public List<OrderResponse> listAllOrders() {
		return store.values().stream().map(OrderResponse::from).collect(Collectors.toList());
	}

	@Override
	public OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus) {
		Order order = findOrThrow(orderId);
		if (!order.getStatus().canTransitionTo(newStatus)) {
			throw new InvalidStatusTransitionException(order.getStatus(), newStatus);
		}
		order.setStatus(newStatus);
		return OrderResponse.from(order);
	}

	private Order findOrThrow(String orderId) {
		Order order = store.get(orderId);
		if (order == null) {
			throw new OrderNotFoundException(orderId);
		}
		return order;
	}
}

package com.Reflection.Order_management_service.service;

import com.Reflection.Order_management_service.dto.CreateOrderRequest;
import com.Reflection.Order_management_service.dto.OrderResponse;
import com.Reflection.Order_management_service.model.OrderStatus;

import java.util.List;

public interface OrderService {

	OrderResponse createOrder(CreateOrderRequest request);

	OrderResponse getOrderById(String orderId);

	OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus);

	List<OrderResponse> listAllOrders();
}

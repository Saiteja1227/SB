package com.example.firstproject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.firstproject.Order;
import com.example.firstproject.OrderItem;
import com.example.firstproject.dto.OrderResponseDTO;
import com.example.firstproject.dto.OrderResponseDTO.OrderItemDTO;

@Service
public class OrderService {
    
    /**
     * Convert Order entity to OrderResponseDTO
     */
    public OrderResponseDTO convertToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        
        dto.setOrderId(order.getId());
        dto.setCustomerName(order.getName());
        dto.setCustomerEmail(order.getEmail());
        dto.setPhone(order.getPhone());
        dto.setAddress(order.getAddress());
        dto.setCity(order.getCity());
        dto.setZipCode(order.getZipCode());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getCreatedAt());
        
        // Convert order items
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
            .map(this::convertItemToDTO)
            .collect(Collectors.toList());
        dto.setItems(itemDTOs);
        
        return dto;
    }
    
    /**
     * Convert OrderItem entity to OrderItemDTO
     */
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        return new OrderItemDTO(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getProduct().getDescription(),
            item.getProduct().getImage(),
            item.getProduct().getCategory(),
            item.getPrice(),
            item.getQuantity()
        );
    }
    
    /**
     * Convert list of orders to DTOs
     */
    public List<OrderResponseDTO> convertToDTOList(List<Order> orders) {
        return orders.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}

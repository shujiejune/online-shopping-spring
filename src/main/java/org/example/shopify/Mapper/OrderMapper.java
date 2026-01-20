package org.example.shopify.Mapper;

import org.example.shopify.DTO.OrderItemDTO;
import org.example.shopify.DTO.OrderRequestDTO;
import org.example.shopify.DTO.OrderResponseDTO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.OrderItem;
import org.example.shopify.Domain.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    private OrderItemDTO mapToOrderItemDTO(OrderItem item) {
        OrderItemDTO itemDto = new OrderItemDTO();
        itemDto.setOrderItemId(item.getId());
        itemDto.setProductId(item.getProduct().getId());
        itemDto.setProductName(item.getProduct().getName());
        itemDto.setQuantity(item.getQuantity());
        itemDto.setPurchasedPrice(item.getPurchasedPrice());
        return itemDto;
    }

    private List<OrderItemDTO> mapToItemDTOList(Order order) {
        return order.getOrderItems().stream()
                .map(this::mapToOrderItemDTO).collect(Collectors.toList());
    }

    public OrderResponseDTO mapToOrderResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();

        dto.setOrderId(order.getId());
        dto.setUsername(order.getUser().getUsername());
        dto.setDatePlaced(order.getDatePlaced());
        dto.setOrderStatus(order.getOrderStatus().toString());
        dto.setTotalAmount(order.getTotalAmount());

        List<OrderItemDTO> itemDtos = mapToItemDTOList(order);
        dto.setItems(itemDtos);

        return dto;
    }

    public List<OrderResponseDTO> mapToOrderResponseDTOList(List<Order> orders) {
        return orders.stream().map(this::mapToOrderResponseDTO).collect(Collectors.toList());
    }
}

package uz.pdp.ecommerce2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.pdp.ecommerce2.dto.OrderItemResponse;
import uz.pdp.ecommerce2.dto.OrderResponse;
import uz.pdp.ecommerce2.model.Order;
import uz.pdp.ecommerce2.model.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderResponse orderToOrderResponse(Order order);
    
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "imageUrl", source = "product.imageUrl")
    @Mapping(target = "subtotal", expression = "java(orderItem.getPrice().multiply(java.math.BigDecimal.valueOf(orderItem.getQuantity())))")
    OrderItemResponse orderItemToOrderItemResponse(OrderItem orderItem);
    
    List<OrderResponse> ordersToOrderResponses(List<Order> orders);
}

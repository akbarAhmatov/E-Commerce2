package uz.pdp.ecommerce2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ecommerce2.dto.*;
import uz.pdp.ecommerce2.exception.BadRequestException;
import uz.pdp.ecommerce2.exception.InsufficientStockException;
import uz.pdp.ecommerce2.exception.ResourceNotFoundException;
import uz.pdp.ecommerce2.exception.UnauthorizedException;
import uz.pdp.ecommerce2.mapper.OrderMapper;
import uz.pdp.ecommerce2.model.*;
import uz.pdp.ecommerce2.repository.CartItemRepository;
import uz.pdp.ecommerce2.repository.OrderRepository;
import uz.pdp.ecommerce2.repository.ProductRepository;
import uz.pdp.ecommerce2.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName()
                );
            }
            total = total.add(
                product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }
        
        Order order = Order.builder()
            .user(user)
            .totalAmount(total)
            .status(OrderStatus.PENDING)
            .shippingAddress(request.getShippingAddress())
            .build();
        
        Order finalOrder = order;
        List<OrderItem> orderItems = cartItems.stream()
            .map(cartItem -> {
                Product product = cartItem.getProduct();
                product.setStock(product.getStock() - cartItem.getQuantity());
                productRepository.save(product);
                
                return OrderItem.builder()
                    .order(finalOrder)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();
            })
            .collect(Collectors.toList());
        
        order.setOrderItems(orderItems);
        order = orderRepository.save(order);

        cartItemRepository.deleteByUserId(userId);
        
        return orderMapper.orderToOrderResponse(order);
    }
    
    public List<OrderResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        return orderMapper.ordersToOrderResponses(orders);
    }
    
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized access");
        }
        
        return orderMapper.orderToOrderResponse(order);
    }
    
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(orderStatus);
            order = orderRepository.save(order);
            return orderMapper.orderToOrderResponse(order);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status");
        }
    }
    
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.ordersToOrderResponses(orders);
    }
}
package uz.pdp.ecommerce2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ecommerce2.model.User;
import uz.pdp.ecommerce2.config.JwtUtil;
import uz.pdp.ecommerce2.dto.*;
import uz.pdp.ecommerce2.exception.DuplicateResourceException;
import uz.pdp.ecommerce2.exception.InsufficientStockException;
import uz.pdp.ecommerce2.exception.ResourceNotFoundException;
import uz.pdp.ecommerce2.exception.UnauthorizedException;
import uz.pdp.ecommerce2.mapper.CartMapper;
import uz.pdp.ecommerce2.mapper.CategoryMapper;
import uz.pdp.ecommerce2.mapper.ProductMapper;
import uz.pdp.ecommerce2.mapper.UserMapper;
import uz.pdp.ecommerce2.model.CartItem;
import uz.pdp.ecommerce2.model.Category;
import uz.pdp.ecommerce2.model.Product;
import uz.pdp.ecommerce2.repository.CartItemRepository;
import uz.pdp.ecommerce2.repository.CategoryRepository;
import uz.pdp.ecommerce2.repository.ProductRepository;
import uz.pdp.ecommerce2.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    
    public CartResponse getCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        List<CartItemResponse> items = cartMapper.cartItemsToCartItemResponses(cartItems);
        
        BigDecimal total = items.stream()
            .map(CartItemResponse::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return CartResponse.builder()
            .items(items)
            .total(total)
            .itemCount(items.size())
            .build();
    }
    
    @Transactional
    public CartItemResponse addToCart(Long userId, CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (product.getStock() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock");
        }
        
        CartItem cartItem = cartItemRepository
            .findByUserIdAndProductId(userId, request.getProductId())
            .orElse(CartItem.builder()
                .user(User.builder().id(userId).build())
                .product(product)
                .quantity(0)
                .build());
        
        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItem = cartItemRepository.save(cartItem);
        
        return cartMapper.cartItemToCartItemResponse(cartItem);
    }
    
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized access");
        }
        
        cartItemRepository.delete(cartItem);
    }
    
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
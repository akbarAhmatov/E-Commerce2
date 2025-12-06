package uz.pdp.ecommerce2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.pdp.ecommerce2.dto.CartItemResponse;
import uz.pdp.ecommerce2.model.CartItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "imageUrl", source = "product.imageUrl")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "subtotal", expression = "java(cartItem.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())))")
    CartItemResponse cartItemToCartItemResponse(CartItem cartItem);
    
    List<CartItemResponse> cartItemsToCartItemResponses(List<CartItem> cartItems);
}

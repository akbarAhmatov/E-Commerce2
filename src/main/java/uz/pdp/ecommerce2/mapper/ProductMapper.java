package uz.pdp.ecommerce2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.pdp.ecommerce2.dto.ProductRequest;
import uz.pdp.ecommerce2.dto.ProductResponse;
import uz.pdp.ecommerce2.model.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    Product productRequestToProduct(ProductRequest request);
    
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categoryId", source = "category.id")
    ProductResponse productToProductResponse(Product product);
    
    List<ProductResponse> productsToProductResponses(List<Product> products);
}

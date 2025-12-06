package uz.pdp.ecommerce2.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private String categoryName;
    private Long categoryId;
    private Boolean active;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
}

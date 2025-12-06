package uz.pdp.ecommerce2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    
    private String description;
}

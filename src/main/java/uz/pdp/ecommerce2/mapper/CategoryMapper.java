package uz.pdp.ecommerce2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.pdp.ecommerce2.dto.CategoryRequest;
import uz.pdp.ecommerce2.dto.CategoryResponse;
import uz.pdp.ecommerce2.model.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Category categoryRequestToCategory(CategoryRequest request);

    @Mapping(target = "productCount",
            expression = "java(category.getProducts() != null ? category.getProducts().size() : 0)")
    CategoryResponse categoryToCategoryResponse(Category category);

    List<CategoryResponse> categoriesToCategoryResponses(List<Category> all);
}

package uz.pdp.ecommerce2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ecommerce2.dto.CategoryRequest;
import uz.pdp.ecommerce2.dto.CategoryResponse;
import uz.pdp.ecommerce2.exception.DuplicateResourceException;
import uz.pdp.ecommerce2.exception.ResourceNotFoundException;
import uz.pdp.ecommerce2.mapper.CategoryMapper;
import uz.pdp.ecommerce2.model.Category;
import uz.pdp.ecommerce2.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.categoriesToCategoryResponses(
            categoryRepository.findAll()
        );
    }
    
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return categoryMapper.categoryToCategoryResponse((Category) category);
    }
    
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists");
        }
        
        Category category = (Category) categoryMapper.categoryRequestToCategory(request);
        category = categoryRepository.save(category);
        
        return categoryMapper.categoryToCategoryResponse((Category) category);
    }
}

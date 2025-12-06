package uz.pdp.ecommerce2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ecommerce2.dto.ProductRequest;
import uz.pdp.ecommerce2.dto.ProductResponse;
import uz.pdp.ecommerce2.exception.ResourceNotFoundException;
import uz.pdp.ecommerce2.mapper.ProductMapper;
import uz.pdp.ecommerce2.model.Category;
import uz.pdp.ecommerce2.model.Product;
import uz.pdp.ecommerce2.repository.CategoryRepository;
import uz.pdp.ecommerce2.repository.ProductRepository;

// PRODUCT SERVICE
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
            .map(productMapper::productToProductResponse);
    }
    
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
            .map(productMapper::productToProductResponse);
    }
    
    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(query, pageable)
            .map(productMapper::productToProductResponse);
    }
    
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.productToProductResponse(product);
    }
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        Product product = productMapper.productRequestToProduct(request);
        product.setCategory(category);
        product = productRepository.save(product);
        
        return productMapper.productToProductResponse(product);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        
        product = productRepository.save(product);
        return productMapper.productToProductResponse(product);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }
}

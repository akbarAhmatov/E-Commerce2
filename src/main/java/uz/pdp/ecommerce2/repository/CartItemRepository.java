package uz.pdp.ecommerce2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.pdp.ecommerce2.model.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserId(Long userId);
    
    @Query("SELECT SUM(ci.quantity * ci.product.price) FROM CartItem ci WHERE ci.user.id = :userId")
    BigDecimal calculateCartTotal(Long userId);
}

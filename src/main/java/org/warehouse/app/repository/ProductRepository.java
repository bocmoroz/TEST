package org.warehouse.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.warehouse.app.entity.Product;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findProductByArticle(String article);

    @Query(value = "SELECT * FROM Product WHERE article LIKE :partialArticle% AND deleted = true", nativeQuery = true)
    List<Product> findByArticleContainingAndDeleted(String partialArticle);

    List<Product> findAllByDeleted(Boolean isDeleted);

}

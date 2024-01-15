package org.warehouse.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.warehouse.app.model.ProductEntity;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findProductByArticle(String article);

    @Query(value = "SELECT * FROM Product WHERE article LIKE :partialArticle% AND deleted = true", nativeQuery = true)
    List<ProductEntity> findByArticleContainingAndDeleted(String partialArticle);

    List<ProductEntity> findAllByDeleted(Boolean isDeleted);

}

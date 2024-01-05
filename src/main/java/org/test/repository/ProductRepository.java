package org.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.entity.Product;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findProductByArticle(String article);

    List<Product> findByArticleContainingAndDeleted(String partialArticle);

    List<Product> findAllByDeleted(Boolean isDeleted);

}

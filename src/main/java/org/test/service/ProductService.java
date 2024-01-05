package org.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dto.product.ProductDto;
import org.test.entity.Product;
import org.test.exception.ProductValidationException;
import org.test.helpers.DeletionCountService;
import org.test.repository.ProductRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final DeletionCountService deletionCountService;

    @Autowired
    public ProductService(ProductRepository productRepository, DeletionCountService deletionCountService) {
        this.productRepository = productRepository;
        this.deletionCountService = deletionCountService;
    }

    public List<ProductDto> getProducts() {
        return productRepository.findAllByDeleted(false)
                .stream()
                .map(ProductDto::create)
                .collect(Collectors.toList());
    }

    public ProductDto getProduct(String article) {
        Product product = getProductByArticle(article);
        return ProductDto.create(product);
    }

    public ProductDto addNewProduct(String article, String name) {
        Optional<Product> existingProduct = productRepository.findProductByArticle(article);
        if (existingProduct.isPresent()) {
            throw new ProductValidationException("Продукт с артикулом " + article + " уже существует!");
        }
        Product product = new Product(article, name);
        productRepository.save(product);

        return ProductDto.create(product);
    }

    @Transactional
    public ProductDto updateProduct(String article, String name) {
        Product productForUpdating = getProductByArticle(article);
        if (productForUpdating.getName().equals(name)) {
            throw new ProductValidationException("Необходимо ввести новое имя продукта!");
        }

        productForUpdating.setName(name);
        productRepository.save(productForUpdating);

        return ProductDto.create(productForUpdating);
    }

    public ProductDto deleteProduct(String article) {
        Product productForDeleting = getProductByArticle(article);
        String deletedArticle = deletionCountService.defineDeletedProductArticle(productForDeleting);
        productForDeleting.setArticle(deletedArticle);
        productForDeleting.setDeleted(true);
        productRepository.save(productForDeleting);

        return ProductDto.create(productForDeleting);
    }

    private Product getProductByArticle(String article) {
        return productRepository.findProductByArticle(article)
                .orElseThrow(() -> new ProductValidationException("Продукт с артикулом " + article + " не существует!"));
    }
}

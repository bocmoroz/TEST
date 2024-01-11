package org.warehouse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dto.product.ProductDto;
import org.warehouse.app.model.ProductEntity;
import org.warehouse.app.exception.ProductValidationException;
import org.warehouse.app.util.DeletionCountService;
import org.warehouse.app.dao.ProductRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final DeletionCountService deletionCountService;

    private final MessageSource messageSource;

    @Autowired
    public ProductService(ProductRepository productRepository, DeletionCountService deletionCountService,
                          MessageSource messageSource) {
        this.productRepository = productRepository;
        this.deletionCountService = deletionCountService;
        this.messageSource = messageSource;
    }

    public List<ProductDto> getProducts() {
        return productRepository.findAllByDeleted(false)
                .stream()
                .map(ProductDto::create)
                .collect(Collectors.toList());
    }

    public ProductDto getProduct(String article) {
        ProductEntity productEntity = getProductByArticle(article);
        return ProductDto.create(productEntity);
    }

    public ProductDto addNewProduct(String article, String name) {
        Optional<ProductEntity> existingProduct = productRepository.findProductByArticle(article);
        if (existingProduct.isPresent()) {
            throw new ProductValidationException(messageSource.getMessage("product.service.already.exists",
                    new Object[]{article}, LocaleContextHolder.getLocale()));
        }
        ProductEntity productEntity = new ProductEntity(article, name);
        productRepository.save(productEntity);

        return ProductDto.create(productEntity);
    }

    @Transactional
    public ProductDto updateProduct(String article, String name) {
        ProductEntity productForUpdating = getProductByArticle(article);
        if (productForUpdating.getName().equals(name)) {
            throw new ProductValidationException(messageSource.getMessage("product.service.invalid.new.name",
                    null, LocaleContextHolder.getLocale()));
        }

        productForUpdating.setName(name);
        productRepository.save(productForUpdating);

        return ProductDto.create(productForUpdating);
    }

    public ProductDto deleteProduct(String article) {
        ProductEntity productForDeleting = getProductByArticle(article);
        String deletedArticle = deletionCountService.defineDeletedProductArticle(productForDeleting);
        productForDeleting.setArticle(deletedArticle);
        productForDeleting.setDeleted(true);
        productRepository.save(productForDeleting);

        return ProductDto.create(productForDeleting);
    }

    private ProductEntity getProductByArticle(String article) {
        return productRepository.findProductByArticle(article)
                .orElseThrow(() -> new ProductValidationException(messageSource.getMessage("product.service.not.exists",
                        new Object[]{article}, LocaleContextHolder.getLocale())));
    }
}

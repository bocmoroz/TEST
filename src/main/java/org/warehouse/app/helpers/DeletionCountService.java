package org.warehouse.app.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.warehouse.app.entity.Product;
import org.warehouse.app.entity.ProductCount;
import org.warehouse.app.entity.Warehouse;
import org.warehouse.app.exception.DeletionValidationException;
import org.warehouse.app.repository.ProductRepository;
import org.warehouse.app.repository.WarehouseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeletionCountService {

    private static final String DELETION_PATTERN = "%s_deleted_%d";

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    private final MessageSource messageSource;

    @Autowired
    public DeletionCountService(ProductRepository productRepository, WarehouseRepository warehouseRepository,
                                MessageSource messageSource) {
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.messageSource = messageSource;
    }

    public String defineDeletedProductArticle(Product product) {
        checkProductDeletionAvailable(product.getArticle());

        int currDeletedCount = 1;
        List<Product> previouslyDeleted = productRepository.findByArticleContainingAndDeleted(product.getArticle());
        if (!previouslyDeleted.isEmpty()) {
            currDeletedCount = previouslyDeleted.size() + 1;
        }

        return String.format(DELETION_PATTERN, product.getArticle(), currDeletedCount);
    }

    private void checkProductDeletionAvailable(String article) {
        List<Warehouse> warehouses = warehouseRepository.findAllByDeleted(false);
        warehouses.forEach(wh -> {
            ProductCount pc = wh.getProducts().stream()
                    .filter(warehouseProduct -> warehouseProduct.getProduct().getArticle().equals(article))
                    .findFirst().orElse(null);
            if (pc != null && pc.getCount() > 0) {
                throw new DeletionValidationException(messageSource.getMessage("deletion.product.exists.in.warehouse",
                        new Object[]{article, wh.getName(), pc.getCount()}, LocaleContextHolder.getLocale()));
            }
        });
    }

    public String defineDeletedWarehouseName(Warehouse warehouse) {
        checkWarehouseDeletionAvailable(warehouse);

        int currDeletedCount = 1;
        List<Warehouse> previouslyDeleted = warehouseRepository.findByNameContainingAndDeleted(warehouse.getName());
        if (!previouslyDeleted.isEmpty()) {
            currDeletedCount = previouslyDeleted.size() + 1;
        }

        return String.format(DELETION_PATTERN, warehouse.getName(), currDeletedCount);
    }

    private void checkWarehouseDeletionAvailable(Warehouse warehouse) {
        List<ProductCount> existingProductsInWarehouse = warehouse.getProducts().stream()
                .filter(productCount -> productCount.getCount() > 0).collect(Collectors.toList());
        if (!existingProductsInWarehouse.isEmpty()) {
            throw new DeletionValidationException(messageSource.getMessage("deletion.warehouse.contains.products",
                    new Object[]{warehouse.getName()},
                    LocaleContextHolder.getLocale()));
        }
    }
}

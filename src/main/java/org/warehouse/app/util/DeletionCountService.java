package org.warehouse.app.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.warehouse.app.model.ProductEntity;
import org.warehouse.app.model.WarehouseProduct;
import org.warehouse.app.model.WarehouseEntity;
import org.warehouse.app.exception.DeletionValidationException;
import org.warehouse.app.dao.ProductRepository;
import org.warehouse.app.dao.WarehouseRepository;

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

    public String defineDeletedProductArticle(ProductEntity productEntity) {
        checkProductDeletionAvailable(productEntity.getArticle());

        int currDeletedCount = 1;
        List<ProductEntity> previouslyDeleted = productRepository.findByArticleContainingAndDeleted(productEntity.getArticle());
        if (!previouslyDeleted.isEmpty()) {
            currDeletedCount = previouslyDeleted.size() + 1;
        }

        return String.format(DELETION_PATTERN, productEntity.getArticle(), currDeletedCount);
    }

    private void checkProductDeletionAvailable(String article) {
        List<WarehouseEntity> warehouses = warehouseRepository.findAllByDeleted(false);
        warehouses.forEach(wh -> {
            WarehouseProduct pc = wh.getProducts().stream()
                    .filter(warehouseProduct -> warehouseProduct.getProduct().getArticle().equals(article))
                    .findFirst().orElse(null);
            if (pc != null && pc.getCount() > 0) {
                throw new DeletionValidationException(messageSource.getMessage("deletion.product.exists.in.warehouse",
                        new Object[]{article, wh.getName(), pc.getCount()}, LocaleContextHolder.getLocale()));
            }
        });
    }

    public String defineDeletedWarehouseName(WarehouseEntity warehouseEntity) {
        checkWarehouseDeletionAvailable(warehouseEntity);

        int currDeletedCount = 1;
        List<WarehouseEntity> previouslyDeleted = warehouseRepository.findByNameContainingAndDeleted(warehouseEntity.getName());
        if (!previouslyDeleted.isEmpty()) {
            currDeletedCount = previouslyDeleted.size() + 1;
        }

        return String.format(DELETION_PATTERN, warehouseEntity.getName(), currDeletedCount);
    }

    private void checkWarehouseDeletionAvailable(WarehouseEntity warehouseEntity) {
        List<WarehouseProduct> existingProductsInWarehouse = warehouseEntity.getProducts().stream()
                .filter(productCount -> productCount.getCount() > 0).collect(Collectors.toList());
        if (!existingProductsInWarehouse.isEmpty()) {
            throw new DeletionValidationException(messageSource.getMessage("deletion.warehouse.contains.products",
                    new Object[]{warehouseEntity.getName()},
                    LocaleContextHolder.getLocale()));
        }
    }
}

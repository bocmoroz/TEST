package org.test.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.test.entity.Product;
import org.test.entity.ProductCount;
import org.test.entity.Warehouse;
import org.test.exception.DeletionValidationException;
import org.test.repository.ProductRepository;
import org.test.repository.WarehouseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DeletionCountService {

    private static final String DELETION_PATTERN = "%s_%s_%d";
    private static final String DELETION_SUFFIX = "deleted";

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Autowired
    public DeletionCountService(ProductRepository productRepository, WarehouseRepository warehouseRepository) {
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public String defineDeletedProductArticle(Product product) {

        checkProductDeletionAvailable(product.getArticle());

        int lastDeletedCount = 0;
        List<Product> previouslyDeleted = productRepository.findByArticleContainingAndDeleted(product.getArticle());
        if (!previouslyDeleted.isEmpty()) {
            lastDeletedCount = previouslyDeleted.size() + 1;
        }

        return String.format(DELETION_PATTERN, product.getArticle(), DELETION_SUFFIX, lastDeletedCount);
    }

    private void checkProductDeletionAvailable(String article) {
        List<Warehouse> warehouses = warehouseRepository.findAllByDeleted(false);
        warehouses.forEach(wh -> {
            ProductCount pc = wh.getProducts().stream()
                    .filter(warehouseProduct -> warehouseProduct.getProduct().getArticle().equals(article))
                    .findFirst().orElse(null);
            if (pc != null && pc.getCount() > 0) {
                throw new DeletionValidationException("Невозможно удалить артикул " + article +
                        " из-за наличия на складе " + wh.getName() + " в количестве " + pc.getCount());
            }
        });
    }

    public String defineDeletedWarehouseName(Warehouse warehouse) {

        checkWarehouseDeletionAvailable(warehouse);

        int lastDeletedCount = 0;
        List<Warehouse> previouslyDeleted = warehouseRepository.findByNameContainingAndDeleted(warehouse.getName());
        if (!previouslyDeleted.isEmpty()) {
            lastDeletedCount = previouslyDeleted.size() + 1;
        }

        return String.format(DELETION_PATTERN, warehouse.getName(), DELETION_SUFFIX, lastDeletedCount);
    }

    private void checkWarehouseDeletionAvailable(Warehouse warehouse) {
        List<ProductCount> existingProductsInWarehouse = warehouse.getProducts().stream()
                .filter(productCount -> productCount.getCount() > 0).collect(Collectors.toList());
        if (!existingProductsInWarehouse.isEmpty()) {
            throw new DeletionValidationException("Невозможно удалить склад " + warehouse.getName() +
                    " из-за наличия на складе: \n" +
                    existingProductsInWarehouse.stream()
                            .map(productCount -> String.format("артикул:%s, кол-во:%d",
                                    productCount.getProduct().getArticle(),
                                    productCount.getCount())));
        }
    }
}

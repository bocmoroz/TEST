package org.test.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.test.entity.Product;
import org.test.entity.Warehouse;
import org.test.repository.ProductRepository;
import org.test.repository.WarehouseRepository;

import java.util.List;

@Component
public class DeleteCountService {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Autowired
    public DeleteCountService(ProductRepository productRepository, WarehouseRepository warehouseRepository) {
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public String defineDeletedProductArticul(Product product) {

        int countDeleted = 0;

        while (true) {

            String deletedArticul = product.getArticul() + ":deleted" + countDeleted;
            List<Product> alreadyDeleted = productRepository.
                    findProductByArticul(deletedArticul);

            if (alreadyDeleted.isEmpty()) {
                return deletedArticul;
            }

            countDeleted++;
        }
    }

    public String defineDeletedWarehouseName(Warehouse warehouse) {

        int countDeleted = 0;

        while (true) {

            String deletedName = warehouse.getName() + ":deleted" + countDeleted;
            List<Warehouse> alreadyDeleted = warehouseRepository.
                    findWarehouseByName(deletedName);

            if (alreadyDeleted.isEmpty()) {
                return deletedName;
            }

            countDeleted++;
        }
    }
}

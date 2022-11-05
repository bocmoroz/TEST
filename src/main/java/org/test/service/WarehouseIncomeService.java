package org.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dto.WarehouseIncomeDto;
import org.test.entity.Product;
import org.test.entity.ProductCount;
import org.test.entity.Warehouse;
import org.test.entity.WarehouseIncome;
import org.test.exception.WarehouseIncomeValidationException;
import org.test.repository.WarehouseIncomeRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseIncomeService {

    private final WarehouseIncomeRepository warehouseIncomeRepository;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseIncomeService(
            WarehouseIncomeRepository warehouseIncomeRepository,
            ProductService productService,
            WarehouseService warehouseService) {
        this.warehouseIncomeRepository = warehouseIncomeRepository;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    public List<WarehouseIncome> getWarehouseIncomes() {
        List<WarehouseIncome> list = warehouseIncomeRepository.findAll();
        if (list.isEmpty()) {
            throw new WarehouseIncomeValidationException("В БД нет поступлений продуктов!");
        }
        return list;
    }

    public WarehouseIncome getWarehouseIncomeById(Long id) {
        List<WarehouseIncome> warehouseIncomes = warehouseIncomeRepository.findWarehouseIncomeById(id);
        if (warehouseIncomes.isEmpty()) {
            throw new WarehouseIncomeValidationException("Поступления с номером " + id + " не существует!");
        }

        return warehouseIncomes.get(0);
    }

    @Transactional
    public WarehouseIncome addNewWarehouseIncome(WarehouseIncomeDto warehouseIncomeDto) {

        Warehouse warehouse = warehouseService.getWarehouseByName(warehouseIncomeDto.getWarehouseName());

        if (warehouse == null) {
            throw new WarehouseIncomeValidationException("Склада с именем "
                    + warehouseIncomeDto.getWarehouseName()
                    + " не существует!");
        }

        warehouseIncomeDto.getProducts().forEach(productIncomeDto -> {
            Product product;
            product = productService.getProductByArticle(productIncomeDto.getArticul());
            if (product != null) {
                product.setLastIncomePrice(productIncomeDto.getPrice());
            } else {
                product = new Product();
                product.setArticul(productIncomeDto.getArticul());
                product.setName(productIncomeDto.getName());
                product.setLastIncomePrice(productIncomeDto.getPrice());
                productService.addNewProduct(product);
            }

            Product finalProduct = product;
            Optional<ProductCount> optionalWarehouseProduct = warehouse.getProducts().stream()
                    .filter(p -> p.getProduct().getArticul().equals(finalProduct.getArticul()))
                    .findAny();

            if (optionalWarehouseProduct.isPresent()) {
                ProductCount warehouseProduct = optionalWarehouseProduct.get();
                warehouseProduct.setCount(warehouseProduct.getCount() + productIncomeDto.getCount());
            } else {
                ProductCount newProductCount = new ProductCount();
                newProductCount.setProduct(product);
                newProductCount.setCount(productIncomeDto.getCount());
                warehouse.getProducts().add(newProductCount);
            }
        });

        WarehouseIncome warehouseIncome = new WarehouseIncome();
        warehouseIncome.setWarehouse(warehouse);
        warehouseIncome.setProducts(warehouseIncomeDto.getProducts().stream()
                .map(productIncomeDto -> {
                    Product prod = productService.getProductByArticle(productIncomeDto.getArticul());

                    ProductCount pc = new ProductCount();
                    pc.setCount(productIncomeDto.getCount());
                    pc.setProduct(prod);
                    return pc;
                }).collect(Collectors.toList()));

        warehouseIncomeRepository.save(warehouseIncome);

        return warehouseIncome;
    }

}

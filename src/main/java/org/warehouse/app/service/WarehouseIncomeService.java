package org.warehouse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dto.income.ProductIncomeDto;
import org.warehouse.app.dto.income.WarehouseIncomeDto;
import org.warehouse.app.entity.Product;
import org.warehouse.app.entity.ProductCount;
import org.warehouse.app.entity.Warehouse;
import org.warehouse.app.entity.WarehouseIncome;
import org.warehouse.app.exception.WarehouseIncomeValidationException;
import org.warehouse.app.repository.ProductRepository;
import org.warehouse.app.repository.WarehouseIncomeRepository;
import org.warehouse.app.repository.WarehouseRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseIncomeService {

    private final WarehouseIncomeRepository warehouseIncomeRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private final MessageSource messageSource;

    @Autowired
    public WarehouseIncomeService(
            WarehouseIncomeRepository warehouseIncomeRepository, WarehouseRepository warehouseRepository,
            ProductRepository productRepository, MessageSource messageSource) {
        this.warehouseIncomeRepository = warehouseIncomeRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.messageSource = messageSource;
    }

    public List<WarehouseIncomeDto> getWarehouseIncomes() {
        return warehouseIncomeRepository.findAll().stream()
                .map(WarehouseIncomeDto::create)
                .collect(Collectors.toList());
    }

    public WarehouseIncomeDto getWarehouseIncomeById(Long id) {
        WarehouseIncome warehouseIncome = warehouseIncomeRepository.findWarehouseIncomeById(id)
                .orElseThrow(() -> new WarehouseIncomeValidationException(messageSource.getMessage(
                        "warehouse.income.service.not.exists", new Object[]{id}, LocaleContextHolder.getLocale())));
        return WarehouseIncomeDto.create(warehouseIncome);
    }

    @Transactional
    public WarehouseIncomeDto addNewWarehouseIncome(String warehouseName, List<ProductIncomeDto> products) {

        Warehouse warehouse = warehouseRepository.findWarehouseByName(warehouseName)
                .orElseThrow(() -> new WarehouseIncomeValidationException(messageSource.getMessage(
                        "warehouse.service.not.exists", new Object[]{warehouseName}, LocaleContextHolder.getLocale())));

        List<Product> existingProducts = productRepository.findAllByDeleted(false);

        List<ProductCount> warehouseIncomeProducts = new ArrayList<>();

        products.forEach(productIncomeDto -> {
            //check income product and add if needed
            Product product = existingProducts.stream()
                    .filter(existingProduct -> existingProduct.getArticle().equals(productIncomeDto.getArticle()))
                    .findFirst().orElse(null);

            if (product != null) {
                product.setLastIncomePrice(productIncomeDto.getPrice());
            } else {
                product = new Product(productIncomeDto.getArticle(), productIncomeDto.getName());
                product.setLastIncomePrice(productIncomeDto.getPrice());
                productRepository.save(product);
            }

            //add or update count of product for warehouse
            Product finalProduct = product;
            List<ProductCount> warehouseProducts = warehouse.getProducts();

            Optional<ProductCount> optionalWarehouseProduct = warehouseProducts.stream()
                    .filter(p -> p.getProduct().getArticle().equals(finalProduct.getArticle()))
                    .findAny();

            if (optionalWarehouseProduct.isPresent()) {
                ProductCount warehouseProduct = optionalWarehouseProduct.get();
                warehouseProduct.setCount(warehouseProduct.getCount() + productIncomeDto.getCount());
            } else {
                ProductCount newProductCount = new ProductCount();
                newProductCount.setProduct(product);
                newProductCount.setCount(productIncomeDto.getCount());
                warehouseProducts.add(newProductCount);
            }

            //add income of product
            ProductCount incomeProductCount = new ProductCount();
            incomeProductCount.setCount(productIncomeDto.getCount());
            incomeProductCount.setProduct(product);
            incomeProductCount.setPrice(productIncomeDto.getPrice());
            warehouseIncomeProducts.add(incomeProductCount);
        });

        //save income
        WarehouseIncome warehouseIncome = new WarehouseIncome();
        warehouseIncome.setWarehouse(warehouse);
        warehouseIncome.setProducts(warehouseIncomeProducts);
        warehouseIncomeRepository.save(warehouseIncome);

        return WarehouseIncomeDto.create(warehouseIncome);
    }

}

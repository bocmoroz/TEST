package org.warehouse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dto.sale.ProductSaleDto;
import org.warehouse.app.dto.sale.WarehouseSaleDto;
import org.warehouse.app.entity.Product;
import org.warehouse.app.entity.ProductCount;
import org.warehouse.app.entity.Warehouse;
import org.warehouse.app.entity.WarehouseSale;
import org.warehouse.app.exception.WarehouseIncomeValidationException;
import org.warehouse.app.exception.WarehouseSaleValidationException;
import org.warehouse.app.repository.ProductRepository;
import org.warehouse.app.repository.WarehouseRepository;
import org.warehouse.app.repository.WarehouseSaleRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseSaleService {

    private final WarehouseSaleRepository warehouseSaleRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private final MessageSource messageSource;

    @Autowired
    public WarehouseSaleService(WarehouseSaleRepository warehouseSaleRepository, WarehouseRepository warehouseRepository, ProductRepository productRepository, MessageSource messageSource) {
        this.warehouseSaleRepository = warehouseSaleRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.messageSource = messageSource;
    }

    public List<WarehouseSaleDto> getWarehouseSales() {
        return warehouseSaleRepository.findAll().stream()
                .map(WarehouseSaleDto::create)
                .collect(Collectors.toList());
    }

    public WarehouseSaleDto getWarehouseSaleById(Long id) {
        WarehouseSale warehouseSale = warehouseSaleRepository.findProductSaleById(id)
                .orElseThrow(() -> new WarehouseSaleValidationException(messageSource.getMessage(
                        "warehouse.sale.service.not.exists", new Object[]{id}, LocaleContextHolder.getLocale())));
        return WarehouseSaleDto.create(warehouseSale);
    }

    @Transactional
    public WarehouseSaleDto addNewWarehouseSale(String warehouseName, List<ProductSaleDto> products) {

        Warehouse warehouse = warehouseRepository.findWarehouseByName(warehouseName)
                .orElseThrow(() -> new WarehouseIncomeValidationException(messageSource.getMessage(
                        "warehouse.service.not.exists", new Object[]{warehouseName}, LocaleContextHolder.getLocale())));

        List<Product> existingProducts = productRepository.findAllByDeleted(false);

        List<ProductCount> warehouseSaleProducts = new ArrayList<>();

        products.forEach(productSaleDto -> {
            //check sale product
            Product product = existingProducts.stream()
                    .filter(existingProduct -> existingProduct.getArticle().equals(productSaleDto.getArticle()))
                    .findFirst().orElse(null);

            if (product != null) {
                product.setLastSalePrice(productSaleDto.getPrice());
            } else {
                throw new WarehouseSaleValidationException(messageSource.getMessage(
                        "product.service.not.exists", new Object[]{productSaleDto.getArticle()},
                        LocaleContextHolder.getLocale()));
            }

            //update count of product for warehouse
            Optional<ProductCount> optionalWarehouseProduct = warehouse.getProducts().stream()
                    .filter(p -> p.getProduct().getArticle().equals(product.getArticle()))
                    .findAny();

            if (optionalWarehouseProduct.isPresent()) {
                ProductCount warehouseProduct = optionalWarehouseProduct.get();
                Integer currentProductCount = warehouseProduct.getCount();
                if (currentProductCount >= productSaleDto.getCount()) {
                    warehouseProduct.setCount(currentProductCount - productSaleDto.getCount());
                } else {
                    throw new WarehouseSaleValidationException(messageSource.getMessage(
                            "warehouse.sale.service.invalid.count.product",
                            new Object[]{productSaleDto.getArticle(), warehouse.getName(), currentProductCount},
                            LocaleContextHolder.getLocale()));
                }
            } else {
                throw new WarehouseSaleValidationException(messageSource.getMessage(
                        "warehouse.sale.service.invalid.product",
                        new Object[]{productSaleDto.getArticle(), warehouse.getName()},
                        LocaleContextHolder.getLocale()));
            }

            //add sale of product
            ProductCount saleProductCount = new ProductCount();
            saleProductCount.setCount(productSaleDto.getCount());
            saleProductCount.setProduct(product);
            warehouseSaleProducts.add(saleProductCount);
        });

        //save sale
        WarehouseSale warehouseSale = new WarehouseSale();
        warehouseSale.setWarehouse(warehouse);
        warehouseSale.setProducts(warehouseSaleProducts);
        warehouseSaleRepository.save(warehouseSale);

        return WarehouseSaleDto.create(warehouseSale);
    }
}

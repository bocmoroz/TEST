package org.warehouse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dto.ProductTransportationDto;
import org.warehouse.app.dto.transportation.WarehouseTransportationDto;
import org.warehouse.app.entity.Product;
import org.warehouse.app.entity.ProductCount;
import org.warehouse.app.entity.Warehouse;
import org.warehouse.app.entity.WarehouseTransportation;
import org.warehouse.app.exception.WarehouseIncomeValidationException;
import org.warehouse.app.exception.WarehouseTransportationValidationException;
import org.warehouse.app.repository.ProductRepository;
import org.warehouse.app.repository.WarehouseRepository;
import org.warehouse.app.repository.WarehouseTransportationRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseTransportationService {

    private final WarehouseTransportationRepository warehouseTransportationRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private final MessageSource messageSource;

    @Autowired
    public WarehouseTransportationService(WarehouseTransportationRepository warehouseTransportationRepository,
                                          WarehouseRepository warehouseRepository, ProductRepository productRepository,
                                          MessageSource messageSource) {
        this.warehouseTransportationRepository = warehouseTransportationRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.messageSource = messageSource;
    }

    public List<WarehouseTransportationDto> getWarehouseTransportations() {
        return warehouseTransportationRepository.findAll().stream()
                .map(WarehouseTransportationDto::create)
                .collect(Collectors.toList());
    }

    public WarehouseTransportationDto getWarehouseTransportationById(Long id) {
        WarehouseTransportation warehouseTransportation = warehouseTransportationRepository.findProductTransportationById(id)
                .orElseThrow(() -> new WarehouseTransportationValidationException(messageSource.getMessage(
                        "warehouse.transportation.service.not.exists", new Object[]{id}, LocaleContextHolder.getLocale())));
        return WarehouseTransportationDto.create(warehouseTransportation);
    }

    @Transactional
    public WarehouseTransportationDto addNewWarehouseTransportation(String warehouseNameFrom, String warehouseNameTo,
                                                                    List<ProductTransportationDto> products) {

        Warehouse warehouseFrom = warehouseRepository.findWarehouseByName(warehouseNameFrom)
                .orElseThrow(() -> new WarehouseIncomeValidationException(messageSource.getMessage(
                        "warehouse.service.not.exists", new Object[]{warehouseNameFrom}, LocaleContextHolder.getLocale())));

        Warehouse warehouseTo = warehouseRepository.findWarehouseByName(warehouseNameTo)
                .orElseThrow(() -> new WarehouseIncomeValidationException(messageSource.getMessage(
                        "warehouse.service.not.exists", new Object[]{warehouseNameTo}, LocaleContextHolder.getLocale())));

        List<Product> existingProducts = productRepository.findAllByDeleted(false);

        List<ProductCount> warehouseTransportationProducts = new ArrayList<>();

        products.forEach(productTransportationDto -> {
            //check transportation product
            Product product = existingProducts.stream()
                    .filter(existingProduct -> existingProduct.getArticle().equals(productTransportationDto.getArticle()))
                    .findFirst().orElse(null);

            if (product == null) {
                throw new WarehouseTransportationValidationException(messageSource.getMessage(
                        "product.service.not.exists", new Object[]{productTransportationDto.getArticle()},
                        LocaleContextHolder.getLocale()));
            }

            //update count of product for warehouses
            Optional<ProductCount> optionalWarehouseFromProduct = warehouseFrom.getProducts().stream()
                    .filter(p -> p.getProduct().getArticle().equals(product.getArticle()))
                    .findAny();

            List<ProductCount> warehouseProductsTo = warehouseTo.getProducts();

            Optional<ProductCount> optionalWarehouseToProduct = warehouseProductsTo.stream()
                    .filter(p -> p.getProduct().getArticle().equals(product.getArticle()))
                    .findAny();

            if (optionalWarehouseFromProduct.isPresent()) {
                ProductCount warehouseFromProduct = optionalWarehouseFromProduct.get();
                Integer currentProductCountInWarehouseFrom = warehouseFromProduct.getCount();
                if (currentProductCountInWarehouseFrom >= productTransportationDto.getCount()) {
                    warehouseFromProduct.setCount(currentProductCountInWarehouseFrom - productTransportationDto.getCount());

                    if (optionalWarehouseToProduct.isPresent()) {
                        ProductCount warehouseToProduct = optionalWarehouseToProduct.get();
                        warehouseToProduct.setCount(warehouseToProduct.getCount() + productTransportationDto.getCount());
                    } else {
                        ProductCount newProductCount = new ProductCount();
                        newProductCount.setProduct(product);
                        newProductCount.setCount(productTransportationDto.getCount());
                        warehouseProductsTo.add(newProductCount);
                    }
                } else {
                    throw new WarehouseTransportationValidationException(messageSource.getMessage(
                            "warehouse.transportation.service.invalid.count.product",
                            new Object[]{productTransportationDto.getArticle(), warehouseFrom.getName(), currentProductCountInWarehouseFrom},
                            LocaleContextHolder.getLocale()));
                }
            } else {
                throw new WarehouseTransportationValidationException(messageSource.getMessage(
                        "warehouse.transportation.service.invalid.product",
                        new Object[]{productTransportationDto.getArticle(), warehouseFrom.getName()},
                        LocaleContextHolder.getLocale()));
            }

            //add transportation of product
            ProductCount transportationProductCount = new ProductCount();
            transportationProductCount.setCount(productTransportationDto.getCount());
            transportationProductCount.setProduct(product);
            warehouseTransportationProducts.add(transportationProductCount);
        });

        //save transportation
        WarehouseTransportation warehouseTransportation = new WarehouseTransportation();
        warehouseTransportation.setWarehouseFrom(warehouseFrom);
        warehouseTransportation.setWarehouseTo(warehouseTo);
        warehouseTransportation.setProducts(warehouseTransportationProducts);
        warehouseTransportationRepository.save(warehouseTransportation);

        return WarehouseTransportationDto.create(warehouseTransportation);
    }

}

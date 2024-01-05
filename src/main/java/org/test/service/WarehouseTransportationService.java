package org.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dto.transportation.ProductTransportationDto;
import org.test.dto.transportation.WarehouseTransportationDto;
import org.test.entity.Product;
import org.test.entity.ProductCount;
import org.test.entity.Warehouse;
import org.test.entity.WarehouseTransportation;
import org.test.exception.WarehouseIncomeValidationException;
import org.test.exception.WarehouseTransportationValidationException;
import org.test.repository.ProductRepository;
import org.test.repository.WarehouseRepository;
import org.test.repository.WarehouseTransportationRepository;

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

    @Autowired
    public WarehouseTransportationService(WarehouseTransportationRepository warehouseTransportationRepository,
                                          ProductService productService,
                                          WarehouseService warehouseService, WarehouseRepository warehouseRepository, ProductRepository productRepository) {
        this.warehouseTransportationRepository = warehouseTransportationRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
    }

    public List<WarehouseTransportationDto> getWarehouseTransportations() {
        return warehouseTransportationRepository.findAll().stream()
                .map(WarehouseTransportationDto::create)
                .collect(Collectors.toList());
    }

    public WarehouseTransportationDto getWarehouseTransportationById(Long id) {
        WarehouseTransportation warehouseTransportation = warehouseTransportationRepository.findProductTransportationById(id)
                .orElseThrow(() -> new WarehouseTransportationValidationException("Перемещения товаров с номером " + id + " не существует!"));
        return WarehouseTransportationDto.create(warehouseTransportation);
    }

    @Transactional
    public WarehouseTransportationDto addNewWarehouseTransportation(String warehouseNameFrom, String warehouseNameTo,
                                                                    List<ProductTransportationDto> products) {

        Warehouse warehouseFrom = warehouseRepository.findWarehouseByName(warehouseNameFrom)
                .orElseThrow(() -> new WarehouseIncomeValidationException("Склад с именем  " + warehouseNameFrom + " не существует!"));

        Warehouse warehouseTo = warehouseRepository.findWarehouseByName(warehouseNameTo)
                .orElseThrow(() -> new WarehouseIncomeValidationException("Склад с именем  " + warehouseNameTo + " не существует!"));

        List<Product> existingProducts = productRepository.findAllByDeleted(false);

        List<ProductCount> warehouseTransportationProducts = new ArrayList<>();

        products.forEach(productTransportationDto -> {
            //check transportation product
            Product product = existingProducts.stream()
                    .filter(existingProduct -> existingProduct.getArticle().equals(productTransportationDto.getArticle()))
                    .findFirst().orElse(null);

            if (product == null) {
                throw new WarehouseTransportationValidationException("Продукта с артикулом "
                        + productTransportationDto.getArticle()
                        + " нет в БД!");
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
                if (warehouseFromProduct.getCount() >= productTransportationDto.getCount()) {
                    warehouseFromProduct.setCount(warehouseFromProduct.getCount() - productTransportationDto.getCount());

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
                    throw new WarehouseTransportationValidationException("Продукта с артикулом "
                            + productTransportationDto.getArticle()
                            + " на складе отправки \""
                            + warehouseFrom.getName()
                            + "\" не имеется в таком количестве!");
                }

            } else {
                throw new WarehouseTransportationValidationException("Продукта с артикулом "
                        + productTransportationDto.getArticle()
                        + " на складе отправки "
                        + warehouseFrom.getName()
                        + " никогда не было!");
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

package org.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dto.transportation.ProductTransportationDto;
import org.test.entity.Product;
import org.test.entity.ProductCount;
import org.test.entity.Warehouse;
import org.test.entity.WarehouseTransportation;
import org.test.exception.WarehouseTransportationValidationException;
import org.test.repository.WarehouseTransportationRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseTransportationService {

    private final WarehouseTransportationRepository warehouseTransportationRepository;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseTransportationService(WarehouseTransportationRepository warehouseTransportationRepository,
                                          ProductService productService,
                                          WarehouseService warehouseService) {
        this.warehouseTransportationRepository = warehouseTransportationRepository;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    public List<WarehouseTransportation> getWarehouseTransportations() {
        List<WarehouseTransportation> list = warehouseTransportationRepository.findAll();
        if (list.isEmpty()) {
            throw new WarehouseTransportationValidationException("В БД нет перемещений продуктов!");
        }
        return list;
    }

    public WarehouseTransportation getWarehouseTransportationById(Long id) {
        List<WarehouseTransportation> warehouseTransportation =
                warehouseTransportationRepository.findProductTransportationById(id);
        if (warehouseTransportation.isEmpty()) {
            throw new WarehouseTransportationValidationException("Перемещения товаров с номером " + id + " не существует!");
        }

        return warehouseTransportation.get(0);
    }

    @Transactional
    public WarehouseTransportation addNewWarehouseTransportation(String warehouseNameFrom, String warehouseNameTo,
                                                                 List<ProductTransportationDto> products) {

        Warehouse warehouseFrom =
                warehouseService.getWarehouseByName(warehouseNameFrom);

        if (warehouseFrom == null) {
            throw new WarehouseTransportationValidationException("Склада с именем "
                    + warehouseNameFrom
                    + " не существует!");
        }

        Warehouse warehouseTo =
                warehouseService.getWarehouseByName(warehouseNameTo);

        if (warehouseTo == null) {
            throw new WarehouseTransportationValidationException("Склада с именем "
                    + warehouseNameTo
                    + " не существует!");
        }

        products.forEach(productTransportationDto -> {
            Product product;
            product = productService.getProductByArticle(productTransportationDto.getArticul());
            if (product == null) {
                throw new WarehouseTransportationValidationException("Продукта с артикулом "
                        + productTransportationDto.getArticul()
                        + " нет в БД!");
            }

            Product finalProduct = product;
            Optional<ProductCount> optionalWarehouseFromProduct = warehouseFrom.getProducts().stream()
                    .filter(p -> p.getProduct().getArticul().equals(finalProduct.getArticul()))
                    .findAny();

            Optional<ProductCount> optionalWarehouseToProduct = warehouseTo.getProducts().stream()
                    .filter(p -> p.getProduct().getArticul().equals(finalProduct.getArticul()))
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
                        warehouseTo.getProducts().add(newProductCount);
                    }

                } else {
                    throw new WarehouseTransportationValidationException("Продукта с артикулом "
                            + productTransportationDto.getArticul()
                            + " на складе отправки "
                            + warehouseFrom.getName()
                            + " не имеется в таком количестве!");
                }

            } else {
                throw new WarehouseTransportationValidationException("Продукта с артикулом "
                        + productTransportationDto.getArticul()
                        + " на складе отправки "
                        + warehouseFrom.getName()
                        + " никогда не было!");
            }
        });

        WarehouseTransportation warehouseTransportation = new WarehouseTransportation();
        warehouseTransportation.setWarehouseFrom(warehouseFrom);
        warehouseTransportation.setWarehouseTo(warehouseTo);
        warehouseTransportation.setProducts(products.stream()
                .map(productTransportationDto -> {
                    Product prod = productService.getProductByArticle(productTransportationDto.getArticul());

                    ProductCount pc = new ProductCount();
                    pc.setCount(productTransportationDto.getCount());
                    pc.setProduct(prod);
                    return pc;
                }).collect(Collectors.toList()));

        warehouseTransportationRepository.save(warehouseTransportation);

        return warehouseTransportation;
    }

}

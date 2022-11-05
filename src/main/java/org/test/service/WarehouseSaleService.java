package org.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dto.WarehouseSaleDto;
import org.test.entity.Product;
import org.test.entity.ProductCount;
import org.test.entity.Warehouse;
import org.test.entity.WarehouseSale;
import org.test.exception.WarehouseSaleValidationException;
import org.test.repository.WarehouseSaleRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseSaleService {

    private final WarehouseSaleRepository warehouseSaleRepository;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseSaleService(WarehouseSaleRepository warehouseSaleRepository,
                                ProductService productService,
                                WarehouseService warehouseService) {
        this.warehouseSaleRepository = warehouseSaleRepository;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    public List<WarehouseSale> getWarehouseSales() {
        List<WarehouseSale> list = warehouseSaleRepository.findAll();
        if (list.isEmpty()) {
            throw new WarehouseSaleValidationException("В БД нет продажи продуктов!");
        }
        return list;
    }

    public WarehouseSale getWarehouseSaleById(Long id) {
        List<WarehouseSale> warehouseSales = warehouseSaleRepository.findProductSaleById(id);
        if (warehouseSales.isEmpty()) {
            throw new WarehouseSaleValidationException("Продажи с номером " + id + " не существует!");
        }

        return warehouseSales.get(0);
    }

    @Transactional
    public WarehouseSale addNewWarehouseSale(WarehouseSaleDto warehouseSaleDto) {

        Warehouse warehouse = warehouseService.getWarehouseByName(warehouseSaleDto.getWarehouseName());

        if (warehouse == null) {
            throw new WarehouseSaleValidationException("Склада с именем "
                    + warehouseSaleDto.getWarehouseName()
                    + " не существует!");
        }

        warehouseSaleDto.getProducts().forEach(productSaleDto -> {
            Product product;
            product = productService.getProductByArticle(productSaleDto.getArticul());
            if (product != null) {
                product.setLastSalePrice(productSaleDto.getPrice());
            } else {
                throw new WarehouseSaleValidationException("Продукта с артикулом " + productSaleDto.getArticul()
                        + " нет в БД!");
            }

            Product finalProduct = product;
            Optional<ProductCount> optionalWarehouseProduct = warehouse.getProducts().stream()
                    .filter(p -> p.getProduct().getArticul().equals(finalProduct.getArticul()))
                    .findAny();

            if (optionalWarehouseProduct.isPresent()) {
                ProductCount warehouseProduct = optionalWarehouseProduct.get();
                if (warehouseProduct.getCount() >= productSaleDto.getCount()) {
                    warehouseProduct.setCount(warehouseProduct.getCount() - productSaleDto.getCount());
                } else {
                    throw new WarehouseSaleValidationException("Продукта с артикулом " + productSaleDto.getArticul()
                            + " на складе продажи " + warehouse.getName() + " не имеется в таком количестве!");
                }

            } else {
                throw new WarehouseSaleValidationException("Продукта с артикулом " + productSaleDto.getArticul()
                        + " на складе продажи" + warehouse.getName() + " никогда не было!");
            }
        });

        WarehouseSale warehouseSale = new WarehouseSale();
        warehouseSale.setWarehouse(warehouse);
        warehouseSale.setProducts(warehouseSaleDto.getProducts().stream()
                .map(productSaleDto -> {
                    Product prod = productService.getProductByArticle(productSaleDto.getArticul());

                    ProductCount pc = new ProductCount();
                    pc.setCount(productSaleDto.getCount());
                    pc.setProduct(prod);
                    return pc;
                }).collect(Collectors.toList()));

        warehouseSaleRepository.save(warehouseSale);

        return warehouseSale;
    }
}

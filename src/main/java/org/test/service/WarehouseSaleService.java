package org.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dto.sale.ProductSaleDto;
import org.test.dto.sale.WarehouseSaleDto;
import org.test.entity.Product;
import org.test.entity.ProductCount;
import org.test.entity.Warehouse;
import org.test.entity.WarehouseSale;
import org.test.exception.WarehouseIncomeValidationException;
import org.test.exception.WarehouseSaleValidationException;
import org.test.repository.ProductRepository;
import org.test.repository.WarehouseRepository;
import org.test.repository.WarehouseSaleRepository;

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

    @Autowired
    public WarehouseSaleService(WarehouseSaleRepository warehouseSaleRepository, WarehouseRepository warehouseRepository, ProductRepository productRepository) {
        this.warehouseSaleRepository = warehouseSaleRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
    }

    public List<WarehouseSaleDto> getWarehouseSales() {
        return warehouseSaleRepository.findAll().stream()
                .map(WarehouseSaleDto::create)
                .collect(Collectors.toList());
    }

    public WarehouseSaleDto getWarehouseSaleById(Long id) {
        WarehouseSale warehouseSale = warehouseSaleRepository.findProductSaleById(id)
                .orElseThrow(() -> new WarehouseSaleValidationException("Продажи с номером " + id + " не существует!"));
        return WarehouseSaleDto.create(warehouseSale);
    }

    @Transactional
    public WarehouseSaleDto addNewWarehouseSale(String warehouseName, List<ProductSaleDto> products) {

        Warehouse warehouse = warehouseRepository.findWarehouseByName(warehouseName)
                .orElseThrow(() -> new WarehouseIncomeValidationException("Склад с именем  " + warehouseName + " не существует!"));

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
                throw new WarehouseSaleValidationException("Продукта с артикулом " + productSaleDto.getArticle()
                        + " нет в БД!");
            }

            //update count of product for warehouse
            Optional<ProductCount> optionalWarehouseProduct = warehouse.getProducts().stream()
                    .filter(p -> p.getProduct().getArticle().equals(product.getArticle()))
                    .findAny();

            if (optionalWarehouseProduct.isPresent()) {
                ProductCount warehouseProduct = optionalWarehouseProduct.get();
                if (warehouseProduct.getCount() >= productSaleDto.getCount()) {
                    warehouseProduct.setCount(warehouseProduct.getCount() - productSaleDto.getCount());
                } else {
                    throw new WarehouseSaleValidationException("Продукта с артикулом " + productSaleDto.getArticle()
                            + " на складе продажи " + warehouse.getName() + " не имеется в таком количестве!");
                }
            } else {
                throw new WarehouseSaleValidationException("Продукта с артикулом " + productSaleDto.getArticle()
                        + " на складе продажи \"" + warehouse.getName() + "\" никогда не было!");
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

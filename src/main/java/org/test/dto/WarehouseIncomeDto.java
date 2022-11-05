package org.test.dto;

import lombok.Data;
import org.test.entity.WarehouseIncome;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class WarehouseIncomeDto {

    private String warehouseName;
    private List<ProductIncomeDto> products;

    public static WarehouseIncomeDto create(WarehouseIncome warehouseIncome) {
        WarehouseIncomeDto warehouseIncomeDto = new WarehouseIncomeDto();

        warehouseIncomeDto.setWarehouseName(warehouseIncome.getWarehouse().getName());
        warehouseIncomeDto.setProducts(warehouseIncome.getProducts().stream().map(
                productCount -> {
                    ProductIncomeDto productDto = new ProductIncomeDto();
                    productDto.setArticul(productCount.getProduct().getArticul());
                    productDto.setName(productCount.getProduct().getName());
                    productDto.setPrice(productCount.getProduct().getLastIncomePrice());
                    productDto.setCount(productCount.getCount());
                    return productDto;
                }).collect(Collectors.toList()));

        return warehouseIncomeDto;
    }
}

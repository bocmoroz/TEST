package org.warehouse.app.dto.income;

import lombok.Data;
import org.warehouse.app.entity.WarehouseIncome;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class WarehouseIncomeDto {

    private Long id;
    private String warehouseName;
    private List<ProductIncomeDto> products;

    public static WarehouseIncomeDto create(WarehouseIncome warehouseIncome) {
        WarehouseIncomeDto warehouseIncomeDto = new WarehouseIncomeDto();

        warehouseIncomeDto.setId(warehouseIncome.getId());
        warehouseIncomeDto.setWarehouseName(warehouseIncome.getWarehouse().getName());
        warehouseIncomeDto.setProducts(warehouseIncome.getProducts().stream().map(
                productCount -> {
                    ProductIncomeDto productDto = new ProductIncomeDto();
                    productDto.setArticle(productCount.getProduct().getArticle());
                    productDto.setName(productCount.getProduct().getName());
                    productDto.setPrice(productCount.getProduct().getLastIncomePrice());
                    productDto.setCount(productCount.getCount());
                    return productDto;
                }).collect(Collectors.toList()));

        return warehouseIncomeDto;
    }
}

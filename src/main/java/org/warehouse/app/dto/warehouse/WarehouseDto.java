package org.warehouse.app.dto.warehouse;

import lombok.Data;
import org.warehouse.app.model.WarehouseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class WarehouseDto {

    private String warehouseName;
    private List<WarehouseProductDto> products;

    public static WarehouseDto create(WarehouseEntity warehouseEntity) {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setWarehouseName(warehouseEntity.getName());
        warehouseDto.setProducts(warehouseEntity.getProducts().stream().map(
                productCount -> {
                    WarehouseProductDto warehouseProductDto = new WarehouseProductDto();
                    warehouseProductDto.setArticle(productCount.getProduct().getArticle());
                    warehouseProductDto.setName(productCount.getProduct().getName());
                    warehouseProductDto.setCount(productCount.getCount());
                    return warehouseProductDto;
                }).collect(Collectors.toList()));

        return warehouseDto;
    }

}

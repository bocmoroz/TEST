package org.warehouse.app.dto.warehouse;

import org.warehouse.app.model.WarehouseEntity;

import java.util.List;
import java.util.stream.Collectors;

public class WarehouseDto {

    private String name;
    private List<WarehouseProductDto> products;

    public static WarehouseDto create(WarehouseEntity warehouseEntity) {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setName(warehouseEntity.getName());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WarehouseProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<WarehouseProductDto> products) {
        this.products = products;
    }
}

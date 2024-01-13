package org.warehouse.app.dto.warehouse;

import org.warehouse.app.model.WarehouseEntity;

public class WarehouseBuilderDto {

    private String name;

    public static WarehouseBuilderDto create(WarehouseEntity warehouseEntity) {
        WarehouseBuilderDto warehouseBuilderDto = new WarehouseBuilderDto();
        warehouseBuilderDto.setName(warehouseEntity.getName());

        return warehouseBuilderDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

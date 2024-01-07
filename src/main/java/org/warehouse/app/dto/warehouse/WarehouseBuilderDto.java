package org.warehouse.app.dto.warehouse;

import lombok.Data;
import org.warehouse.app.entity.Warehouse;

@Data
public class WarehouseBuilderDto {

    private String name;

    public static WarehouseBuilderDto create(Warehouse warehouse) {
        WarehouseBuilderDto warehouseBuilderDto = new WarehouseBuilderDto();
        warehouseBuilderDto.setName(warehouse.getName());

        return warehouseBuilderDto;
    }
}

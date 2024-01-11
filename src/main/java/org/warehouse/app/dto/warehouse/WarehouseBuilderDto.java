package org.warehouse.app.dto.warehouse;

import lombok.Data;
import org.warehouse.app.model.WarehouseEntity;

@Data
public class WarehouseBuilderDto {

    private String name;

    public static WarehouseBuilderDto create(WarehouseEntity warehouseEntity) {
        WarehouseBuilderDto warehouseBuilderDto = new WarehouseBuilderDto();
        warehouseBuilderDto.setName(warehouseEntity.getName());

        return warehouseBuilderDto;
    }
}

package org.warehouse.app.dto.transportation;

import lombok.Data;
import org.warehouse.app.dto.ProductTransportationDto;

import java.util.List;

@Data
public class WarehouseTransportationBuilderDto {

    private String warehouseNameFrom;
    private String warehouseNameTo;
    private List<ProductTransportationDto> products;

}

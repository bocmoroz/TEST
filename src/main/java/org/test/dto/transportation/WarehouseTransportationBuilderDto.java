package org.test.dto.transportation;

import lombok.Data;

import java.util.List;

@Data
public class WarehouseTransportationBuilderDto {

    private String warehouseNameFrom;
    private String warehouseNameTo;
    private List<ProductTransportationDto> products;

}

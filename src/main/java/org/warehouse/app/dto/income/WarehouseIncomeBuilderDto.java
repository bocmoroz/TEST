package org.warehouse.app.dto.income;

import lombok.Data;

import java.util.List;

@Data
public class WarehouseIncomeBuilderDto {

    private String warehouseName;
    private List<ProductIncomeDto> products;

}

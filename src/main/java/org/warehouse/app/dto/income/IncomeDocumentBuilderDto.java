package org.warehouse.app.dto.income;

import lombok.Data;

import java.util.List;

@Data
public class IncomeDocumentBuilderDto {

    private String documentName;
    private String warehouseName;
    private List<IncomeDocumentBuilderProductDto> products;

}

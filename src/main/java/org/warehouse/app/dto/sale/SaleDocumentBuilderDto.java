package org.warehouse.app.dto.sale;

import lombok.Data;

import java.util.List;

@Data
public class SaleDocumentBuilderDto {

    private String documentName;
    private String warehouseName;
    private List<SaleDocumentBuilderProductDto> products;

}

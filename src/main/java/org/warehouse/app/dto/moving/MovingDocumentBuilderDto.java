package org.warehouse.app.dto.moving;

import lombok.Data;

import java.util.List;

@Data
public class MovingDocumentBuilderDto {

    private String documentName;
    private String warehouseNameFrom;
    private String warehouseNameTo;
    private List<MovingDocumentBuilderProductDto> products;

}

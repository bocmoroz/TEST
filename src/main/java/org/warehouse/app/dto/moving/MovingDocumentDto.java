package org.warehouse.app.dto.moving;

import lombok.Data;
import org.warehouse.app.dto.TransportationDocumentDto;
import org.warehouse.app.model.TransportationDocumentEntity;

@Data
public class MovingDocumentDto extends TransportationDocumentDto {

    private String warehouseNameFrom;
    private String warehouseNameTo;

    public MovingDocumentDto(TransportationDocumentEntity transportationDocument) {
        init(transportationDocument);
    }

    @Override
    protected void init(TransportationDocumentEntity transportationDocument) {
        super.init(transportationDocument);
        this.warehouseNameFrom = transportationDocument.getWarehouseFrom().getName();
        this.warehouseNameTo = transportationDocument.getWarehouseTo().getName();
    }
}

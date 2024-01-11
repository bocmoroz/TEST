package org.warehouse.app.dto.sale;

import lombok.Data;
import org.warehouse.app.dto.TransportationDocumentDto;
import org.warehouse.app.model.TransportationDocumentEntity;

@Data
public class SaleDocumentDto extends TransportationDocumentDto {

    private String warehouseName;

    public SaleDocumentDto(TransportationDocumentEntity transportationDocument) {
        init(transportationDocument);
    }

    @Override
    protected void init(TransportationDocumentEntity transportationDocument) {
        super.init(transportationDocument);
        this.warehouseName = transportationDocument.getWarehouseFrom().getName();
    }
}

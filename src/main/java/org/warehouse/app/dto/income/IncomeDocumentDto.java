package org.warehouse.app.dto.income;

import lombok.Data;
import org.warehouse.app.dto.TransportationDocumentDto;
import org.warehouse.app.model.TransportationDocumentEntity;

@Data
public class IncomeDocumentDto extends TransportationDocumentDto {

    private String warehouseName;

    public IncomeDocumentDto(TransportationDocumentEntity transportationDocument) {
        init(transportationDocument);
    }

    @Override
    protected void init(TransportationDocumentEntity transportationDocument) {
        super.init(transportationDocument);
        this.warehouseName = transportationDocument.getWarehouseTo().getName();
    }
}

package org.warehouse.app.dto.transportation;

import org.warehouse.app.enums.TransportationDocumentTypeEnum;

import java.util.List;

public class TransportationDocumentBuilderDto {

    private String documentName;
    private String warehouseNameFrom;
    private String warehouseNameTo;
    private List<TransportationDocumentBuilderProductDto> products;
    private TransportationDocumentTypeEnum type;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getWarehouseNameFrom() {
        return warehouseNameFrom;
    }

    public void setWarehouseNameFrom(String warehouseNameFrom) {
        this.warehouseNameFrom = warehouseNameFrom;
    }

    public String getWarehouseNameTo() {
        return warehouseNameTo;
    }

    public void setWarehouseNameTo(String warehouseNameTo) {
        this.warehouseNameTo = warehouseNameTo;
    }

    public List<TransportationDocumentBuilderProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<TransportationDocumentBuilderProductDto> products) {
        this.products = products;
    }

    public TransportationDocumentTypeEnum getType() {
        return type;
    }

    public void setType(TransportationDocumentTypeEnum type) {
        this.type = type;
    }
}

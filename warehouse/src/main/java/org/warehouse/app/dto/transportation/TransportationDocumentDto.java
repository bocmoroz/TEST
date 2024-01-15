package org.warehouse.app.dto.transportation;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.warehouse.app.dto.DocumentDto;
import org.warehouse.app.enums.TransportationDocumentTypeEnum;
import org.warehouse.app.model.TransportationDocumentEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TransportationDocumentDto implements DocumentDto {

    private Long id;
    private String name;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime createdDate;
    private String warehouseNameFrom;
    private String warehouseNameTo;
    private List<DocumentProductDto> products;
    private TransportationDocumentTypeEnum type;

    public TransportationDocumentDto(TransportationDocumentEntity transportationDocument) {
        init(transportationDocument);
    }

    private void init(TransportationDocumentEntity transportationDocument) {
        this.id = transportationDocument.getId();
        this.name = transportationDocument.getName();
        this.createdDate = transportationDocument.getCreatedDate();
        this.warehouseNameFrom = transportationDocument.getWarehouseFrom() != null ?
                transportationDocument.getWarehouseFrom().getName() : null;
        this.warehouseNameTo = transportationDocument.getWarehouseTo() != null ?
                transportationDocument.getWarehouseTo().getName() : null;
        this.type = transportationDocument.getType();
        this.products = transportationDocument.getProducts().stream().map(
                documentProduct -> {
                    DocumentProductDto productDto = new DocumentProductDto();
                    productDto.setArticle(documentProduct.getProduct().getArticle());
                    productDto.setName(documentProduct.getProduct().getName());
                    productDto.setCount(documentProduct.getCount());
                    productDto.setPrice(documentProduct.getPrice());
                    return productDto;
                }).collect(Collectors.toList());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getWarehouseNameFrom() {
        return warehouseNameFrom;
    }

    @Override
    public String getWarehouseNameTo() {
        return warehouseNameTo;
    }

    @Override
    public List<DocumentProductDto> getProducts() {
        return products;
    }

    @Override
    public TransportationDocumentTypeEnum getType() {
        return type;
    }

}

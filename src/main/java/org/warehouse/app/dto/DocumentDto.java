package org.warehouse.app.dto;

import org.warehouse.app.dto.transportation.DocumentProductDto;
import org.warehouse.app.enums.TransportationDocumentTypeEnum;

import java.time.LocalDateTime;
import java.util.List;

public interface DocumentDto {

    Long getId();

    String getName();

    LocalDateTime getCreatedDate();

    String getWarehouseNameFrom();

    String getWarehouseNameTo();

    List<DocumentProductDto> getProducts();

    TransportationDocumentTypeEnum getType();

}

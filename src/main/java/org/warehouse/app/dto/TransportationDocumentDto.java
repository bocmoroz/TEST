package org.warehouse.app.dto;

import lombok.Data;
import org.warehouse.app.enums.TransportationDocumentTypeEnum;
import org.warehouse.app.model.TransportationDocumentEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public abstract class TransportationDocumentDto {

    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private List<DocumentProductDto> products;
    private TransportationDocumentTypeEnum type;

    protected void init(TransportationDocumentEntity transportationDocument) {
        this.id = transportationDocument.getId();
        this.name = transportationDocument.getName();
        this.createdDate = transportationDocument.getCreatedDate();
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

    @Data
    private static class DocumentProductDto {
        private String article;
        private String name;
        private Integer count;
        private BigDecimal price;
    }
}

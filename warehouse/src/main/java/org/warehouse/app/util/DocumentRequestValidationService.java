package org.warehouse.app.util;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dto.transportation.TransportationDocumentBuilderDto;
import org.warehouse.app.dto.transportation.TransportationDocumentBuilderProductDto;
import org.warehouse.app.enums.TransportationDocumentTypeEnum;
import org.warehouse.app.exception.TransportationDocumentValidationException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentRequestValidationService {

    private final MessageSource messageSource;

    @Autowired
    public DocumentRequestValidationService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void validateTransportationDocumentRequest(TransportationDocumentBuilderDto dto) {
        validateDocumentName(dto.getDocumentName());
        validateDocumentType(dto.getType());
        validateDocumentProducts(dto.getProducts(), dto.getType());
        validateWarehouseNames(dto.getWarehouseNameFrom(), dto.getWarehouseNameTo(), dto.getType());
    }

    private void validateDocumentName(String documentName) {
        if (StringUtils.isEmpty(documentName)) {
            throw new TransportationDocumentValidationException(messageSource.getMessage(
                    "validation.add.transportation.document.invalid.document.name",
                    null, LocaleContextHolder.getLocale()));
        }
    }

    private void validateDocumentType(TransportationDocumentTypeEnum type) {
        if (type == null || !Arrays.asList(TransportationDocumentTypeEnum.values()).contains(type)) {
            throw new TransportationDocumentValidationException(messageSource.getMessage(
                    "validation.add.transportation.document.invalid.document.type",
                    null, LocaleContextHolder.getLocale()));
        }
    }

    private void validateDocumentProducts(List<TransportationDocumentBuilderProductDto> products,
                                          TransportationDocumentTypeEnum type) {
        if (products == null || products.isEmpty()) {
            throw new TransportationDocumentValidationException(messageSource.getMessage(
                    "validation.add.transportation.document.invalid.products",
                    null, LocaleContextHolder.getLocale()));
        }

        List<TransportationDocumentBuilderProductDto> invalidDtoList = products.stream()
                .filter(product -> isProductInvalid(product, type))
                .collect(Collectors.toList());

        if (!invalidDtoList.isEmpty()) {
            String invalidProductsString = invalidDtoList.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
            throw new TransportationDocumentValidationException(messageSource.getMessage(
                    "validation.add.transportation.document.invalid.products.fields",
                    new Object[]{invalidProductsString}, LocaleContextHolder.getLocale()));
        }
    }

    private boolean isProductInvalid(TransportationDocumentBuilderProductDto product, TransportationDocumentTypeEnum type) {
        if (type.isContainsPrice()) {
            return product.containsInvalidCommonFields() || product.containsInvalidPriceField();
        }
        return product.containsInvalidCommonFields();
    }

    private void validateWarehouseNames(String warehouseNameFrom, String warehouseNameTo,
                                        TransportationDocumentTypeEnum type) {
        switch (type) {
            case INCOME:
                checkWarehouseName(warehouseNameTo);
                break;
            case SALE:
                checkWarehouseName(warehouseNameFrom);
                break;
            case MOVING:
                checkWarehouseName(warehouseNameFrom);
                checkWarehouseName(warehouseNameTo);
                break;
            default:
                throw new TransportationDocumentValidationException(messageSource.getMessage(
                        "validation.add.transportation.document.invalid.document.type",
                        null, LocaleContextHolder.getLocale()));
        }
    }

    private void checkWarehouseName(String warehouseName) {
        if (StringUtils.isEmpty(warehouseName)) {
            throw new TransportationDocumentValidationException(messageSource.getMessage(
                    "validation.add.transportation.document.invalid.warehouse.name",
                    null, LocaleContextHolder.getLocale()));
        }
    }
}

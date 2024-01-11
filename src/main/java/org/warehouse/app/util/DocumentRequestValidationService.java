package org.warehouse.app.util;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dto.moving.MovingDocumentBuilderProductDto;
import org.warehouse.app.dto.income.IncomeDocumentBuilderProductDto;
import org.warehouse.app.dto.sale.SaleDocumentBuilderProductDto;
import org.warehouse.app.exception.IncomeDocumentValidationException;
import org.warehouse.app.exception.SaleDocumentValidationException;
import org.warehouse.app.exception.MovingDocumentValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentRequestValidationService {

    private final MessageSource messageSource;

    @Autowired
    public DocumentRequestValidationService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void validateIncomeDocumentRequest(String documentName, String warehouseName, List<IncomeDocumentBuilderProductDto> products) {

        if (StringUtils.isEmpty(documentName)) {
            throw new IncomeDocumentValidationException(messageSource.getMessage(
                    "validation.add.income.document.invalid.document.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (StringUtils.isEmpty(warehouseName)) {
            throw new IncomeDocumentValidationException(messageSource.getMessage(
                    "validation.add.income.document.invalid.warehouse.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (products == null || products.isEmpty()) {
            throw new IncomeDocumentValidationException(messageSource.getMessage(
                    "validation.add.income.document.invalid.products",
                    null, LocaleContextHolder.getLocale()));
        }

        List<IncomeDocumentBuilderProductDto> invalidDtoList = products.stream()
                .filter(IncomeDocumentBuilderProductDto::checkFieldsForUploading)
                .collect(Collectors.toList());

        if (!invalidDtoList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            invalidDtoList.forEach(productIncomeDto -> sb.append(productIncomeDto.toString()).append("\n"));
            throw new IncomeDocumentValidationException(messageSource.getMessage(
                    "validation.add.income.document.invalid.products.fields",
                    new Object[]{sb.toString()}, LocaleContextHolder.getLocale()));
        }
    }

    public void validateSaleDocumentRequest(String documentName, String warehouseName,
                                            List<SaleDocumentBuilderProductDto> products) {

        if (StringUtils.isEmpty(documentName)) {
            throw new SaleDocumentValidationException(messageSource.getMessage(
                    "validation.add.sale.document.invalid.document.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (StringUtils.isEmpty(warehouseName)) {
            throw new SaleDocumentValidationException(messageSource.getMessage(
                    "validation.add.sale.document.invalid.warehouse.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (products == null || products.isEmpty()) {
            throw new SaleDocumentValidationException(messageSource.getMessage(
                    "validation.add.sale.document.invalid.products",
                    null, LocaleContextHolder.getLocale()));
        }

        List<SaleDocumentBuilderProductDto> invalidDtoList = products.stream()
                .filter(SaleDocumentBuilderProductDto::checkFieldsForUploading)
                .collect(Collectors.toList());

        if (!invalidDtoList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            invalidDtoList.forEach(productSaleDto -> sb.append(productSaleDto.toString()).append("\n"));
            throw new SaleDocumentValidationException(messageSource.getMessage(
                    "validation.add.sale.document.invalid.products.fields",
                    new Object[]{sb.toString()}, LocaleContextHolder.getLocale()));
        }

    }

    public void validateMovingDocumentRequest(String documentName, String warehouseNameFrom, String warehouseNameTo,
                                              List<MovingDocumentBuilderProductDto> products) {

        if (StringUtils.isEmpty(documentName)) {
            throw new MovingDocumentValidationException(messageSource.getMessage(
                    "validation.add.moving.document.invalid.document.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (StringUtils.isEmpty(warehouseNameFrom)) {
            throw new MovingDocumentValidationException(messageSource.getMessage(
                    "validation.add.moving.document.invalid.warehouse.from.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (StringUtils.isEmpty(warehouseNameTo)) {
            throw new MovingDocumentValidationException(messageSource.getMessage(
                    "validation.add.moving.document.invalid.warehouse.to.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (products == null || products.isEmpty()) {
            throw new MovingDocumentValidationException(messageSource.getMessage(
                    "validation.add.moving.document.invalid.products",
                    null, LocaleContextHolder.getLocale()));
        }

        List<MovingDocumentBuilderProductDto> invalidDtoList = products.stream()
                .filter(MovingDocumentBuilderProductDto::checkFieldsForUploading)
                .collect(Collectors.toList());

        if (!invalidDtoList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            invalidDtoList.forEach(productMovingDto -> sb.append(productMovingDto.toString()).append("\n"));
            throw new MovingDocumentValidationException(messageSource.getMessage(
                    "validation.add.moving.document.invalid.products.fields",
                    new Object[]{sb.toString()}, LocaleContextHolder.getLocale()));
        }

    }
}

package org.warehouse.app.helpers;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dto.ProductTransportationDto;
import org.warehouse.app.dto.income.ProductIncomeDto;
import org.warehouse.app.dto.sale.ProductSaleDto;
import org.warehouse.app.exception.WarehouseIncomeValidationException;
import org.warehouse.app.exception.WarehouseSaleValidationException;
import org.warehouse.app.exception.WarehouseTransportationValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentRequestValidationService {

    private final MessageSource messageSource;

    @Autowired
    public DocumentRequestValidationService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void validateProductIncomeRequest(String warehouseName, List<ProductIncomeDto> products) {

        if (StringUtils.isEmpty(warehouseName)) {
            throw new WarehouseIncomeValidationException(messageSource.getMessage(
                    "validation.add.product.income.invalid.warehouse.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (products == null || products.isEmpty()) {
            throw new WarehouseIncomeValidationException(messageSource.getMessage(
                    "validation.add.product.income.invalid.products",
                    null, LocaleContextHolder.getLocale()));
        }

        List<ProductIncomeDto> invalidDtoList = products.stream()
                .filter(ProductIncomeDto::checkFieldsForUploading)
                .collect(Collectors.toList());

        if (!invalidDtoList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            invalidDtoList.forEach(productIncomeDto -> sb.append(productIncomeDto.toString()).append("\n"));
            throw new WarehouseIncomeValidationException(messageSource.getMessage(
                    "validation.add.product.income.invalid.products.fields",
                    new Object[]{sb.toString()}, LocaleContextHolder.getLocale()));
        }
    }

    public void validateProductSaleRequest(String warehouseName, List<ProductSaleDto> products) {

        if (StringUtils.isEmpty(warehouseName)) {
            throw new WarehouseSaleValidationException(messageSource.getMessage(
                    "validation.add.product.sale.invalid.warehouse.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (products == null || products.isEmpty()) {
            throw new WarehouseSaleValidationException(messageSource.getMessage(
                    "validation.add.product.sale.invalid.products",
                    null, LocaleContextHolder.getLocale()));
        }

        List<ProductSaleDto> invalidDtoList = products.stream()
                .filter(ProductTransportationDto::checkFieldsForUploading)
                .collect(Collectors.toList());

        if (!invalidDtoList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            invalidDtoList.forEach(productSaleDto -> sb.append(productSaleDto.toString()).append("\n"));
            throw new WarehouseSaleValidationException(messageSource.getMessage(
                    "validation.add.product.sale.invalid.products.fields",
                    new Object[]{sb.toString()}, LocaleContextHolder.getLocale()));
        }

    }

    public void validateProductTransportationRequest(String warehouseNameFrom, String warehouseNameTo,
                                                     List<ProductTransportationDto> products) {

        if (StringUtils.isEmpty(warehouseNameFrom)) {
            throw new WarehouseSaleValidationException(messageSource.getMessage(
                    "validation.add.product.transportation.invalid.warehouse.from.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (StringUtils.isEmpty(warehouseNameTo)) {
            throw new WarehouseSaleValidationException(messageSource.getMessage(
                    "validation.add.product.transportation.invalid.warehouse.to.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (products == null || products.isEmpty()) {
            throw new WarehouseSaleValidationException(messageSource.getMessage(
                    "validation.add.product.transportation.invalid.products",
                    null, LocaleContextHolder.getLocale()));
        }

        List<ProductTransportationDto> invalidDtoList = products.stream()
                .filter(ProductTransportationDto::checkFieldsForUploading)
                .collect(Collectors.toList());

        if (!invalidDtoList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            invalidDtoList.forEach(productMovingDto -> sb.append(productMovingDto.toString()).append("\n"));
            throw new WarehouseTransportationValidationException(messageSource.getMessage(
                    "validation.add.product.transportation.invalid.products.fields",
                    new Object[]{sb.toString()}, LocaleContextHolder.getLocale()));
        }

    }
}

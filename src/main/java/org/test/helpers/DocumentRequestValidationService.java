package org.test.helpers;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Service;
import org.test.dto.income.ProductIncomeDto;
import org.test.dto.sale.ProductSaleDto;
import org.test.dto.transportation.ProductTransportationDto;
import org.test.exception.WarehouseIncomeValidationException;
import org.test.exception.WarehouseSaleValidationException;
import org.test.exception.WarehouseTransportationValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentRequestValidationService {

    public void validateProductIncomeRequest(String warehouseName, List<ProductIncomeDto> products) {

        if (StringUtils.isEmpty(warehouseName)) {
            throw new WarehouseIncomeValidationException("Неправильный запрос на добавление поступления продуктов!");
        }

        if (products == null || products.isEmpty()) {
            throw new WarehouseIncomeValidationException("Неправильный запрос на добавление поступления продуктов!");
        }

        List<ProductIncomeDto> invalidDtoList = products.stream()
                .filter(p -> StringUtils.isEmpty(p.getArticle()))
                .filter(p -> StringUtils.isEmpty(p.getName()))
                .filter(p -> (p.getPrice() == null || p.getPrice() < 0))
                .filter(p -> (p.getCount() == null || p.getCount() < 0))
                .collect(Collectors.toList());

        if (!invalidDtoList.isEmpty()) {
            throw new WarehouseIncomeValidationException("Неправильный запрос на добавление поступления продуктов!");
        }

    }

    public void validateProductSaleRequest(String warehouseName, List<ProductSaleDto> products) {

        if (StringUtils.isEmpty(warehouseName)) {
            throw new WarehouseSaleValidationException("Неправильный запрос на добавление продажи продуктов!");
        }

        if (products == null || products.isEmpty()) {
            throw new WarehouseSaleValidationException("Неправильный запрос на добавление продажи продуктов!");
        }

        List<ProductSaleDto> invalidDtoList = products.stream()
                .filter(p -> StringUtils.isEmpty(p.getArticle()))
                .filter(p -> StringUtils.isEmpty(p.getName()))
                .filter(p -> (p.getPrice() == null || p.getPrice() < 0))
                .filter(p -> (p.getCount() == null || p.getCount() < 0))
                .collect(Collectors.toList());

        if (!invalidDtoList.isEmpty()) {
            throw new WarehouseSaleValidationException("Неправильный запрос на добавление продажи продуктов!");
        }

    }

    public void validateProductTransportationRequest(String warehouseNameFrom, String warehouseNameTo,
                                                     List<ProductTransportationDto> products) {

        if (StringUtils.isEmpty(warehouseNameFrom)) {
            throw new WarehouseSaleValidationException("Неправильный запрос на добавление перемещения продуктов!");
        }

        if (StringUtils.isEmpty(warehouseNameTo)) {
            throw new WarehouseSaleValidationException("Неправильный запрос на добавление перемещения продуктов!");
        }

        if (products == null || products.isEmpty()) {
            throw new WarehouseSaleValidationException("Неправильный запрос на добавление перемещения продуктов!");
        }

        List<ProductTransportationDto> invalidDtoList = products.stream()
                .filter(p -> StringUtils.isEmpty(p.getArticle()))
                .filter(p -> StringUtils.isEmpty(p.getName()))
                .filter(p -> (p.getCount() == null || p.getCount() < 0))
                .collect(Collectors.toList());

        if (!invalidDtoList.isEmpty()) {
            throw new WarehouseTransportationValidationException("Неправильный запрос на добавление перемещения продуктов!");
        }

    }
}

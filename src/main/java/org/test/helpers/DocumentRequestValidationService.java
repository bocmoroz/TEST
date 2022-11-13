package org.test.helpers;

import org.springframework.stereotype.Component;
import org.test.dto.income.ProductIncomeDto;
import org.test.dto.income.WarehouseIncomeBuilderDto;
import org.test.dto.sale.ProductSaleDto;
import org.test.dto.sale.WarehouseSaleBuilderDto;
import org.test.dto.transportation.ProductTransportationDto;
import org.test.dto.transportation.WarehouseTransportationBuilderDto;
import org.test.exception.WarehouseIncomeValidationException;
import org.test.exception.WarehouseTransportationValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentRequestValidationService {

    public void validateProductIncomeRequest(WarehouseIncomeBuilderDto warehouseIncomeBuilderDto) {

        if (warehouseIncomeBuilderDto.getWarehouseName() == null || warehouseIncomeBuilderDto.getProducts() == null
                || warehouseIncomeBuilderDto.getProducts().isEmpty()) {
            throw new WarehouseIncomeValidationException("Неправильный запрос на добавление поступления продуктов!");
        }

        List<ProductIncomeDto> list = warehouseIncomeBuilderDto.getProducts().stream()
                .filter(p -> p.getArticul() == null)
                .filter(p -> p.getArticul().isEmpty())
                .filter(p -> p.getName() == null)
                .filter(p -> p.getName().isEmpty())
                .filter(p -> p.getPrice() == null)
                .filter(p -> p.getPrice() > 0)
                .filter(p -> p.getCount() == null)
                .filter(p -> p.getCount() > 0)
                .collect(Collectors.toList());

        if (!list.isEmpty()) {
            throw new WarehouseIncomeValidationException("Неправильный запрос на добавление поступления продуктов!");
        }

    }

    public void validateProductSaleRequest(WarehouseSaleBuilderDto warehouseSaleBuilderDto) {

        if (warehouseSaleBuilderDto.getWarehouseName() == null || warehouseSaleBuilderDto.getProducts() == null
                || warehouseSaleBuilderDto.getProducts().isEmpty()) {
            throw new WarehouseIncomeValidationException("Неправильный запрос на добавление продажи продуктов!");
        }

        List<ProductSaleDto> list = warehouseSaleBuilderDto.getProducts().stream()
                .filter(p -> p.getArticul() == null)
                .filter(p -> p.getArticul().isEmpty())
                .filter(p -> p.getName() == null)
                .filter(p -> p.getName().isEmpty())
                .filter(p -> p.getPrice() == null)
                .filter(p -> p.getPrice() > 0)
                .filter(p -> p.getCount() == null)
                .filter(p -> p.getCount() > 0)
                .collect(Collectors.toList());

        if (!list.isEmpty()) {
            throw new WarehouseIncomeValidationException("Неправильный запрос на добавление продажи продуктов!");
        }

    }

    public void validateProductTransportationRequest(WarehouseTransportationBuilderDto warehouseTransportationBuilderDto) {

        if (warehouseTransportationBuilderDto.getWarehouseNameFrom() == null
                || warehouseTransportationBuilderDto.getWarehouseNameTo() == null
                || warehouseTransportationBuilderDto.getProducts() == null
                || warehouseTransportationBuilderDto.getProducts().isEmpty()) {
            throw new WarehouseTransportationValidationException("Неправильный запрос на добавление перемещения продуктов!");
        }

        List<ProductTransportationDto> list = warehouseTransportationBuilderDto.getProducts().stream()
                .filter(p -> p.getArticul() == null)
                .filter(p -> p.getArticul().isEmpty())
                .filter(p -> p.getName() == null)
                .filter(p -> p.getName().isEmpty())
                .filter(p -> p.getCount() == null)
                .filter(p -> p.getCount() > 0)
                .collect(Collectors.toList());

        if (!list.isEmpty()) {
            throw new WarehouseTransportationValidationException("Неправильный запрос на добавление перемещения продуктов!");
        }

    }
}

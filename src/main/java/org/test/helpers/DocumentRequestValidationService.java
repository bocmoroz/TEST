package org.test.helpers;

import org.springframework.stereotype.Component;
import org.test.dto.*;
import org.test.exception.WarehouseIncomeValidationException;
import org.test.exception.WarehouseTransportationValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentRequestValidationService {

    public void validateProductIncomeRequest(WarehouseIncomeDto warehouseIncomeDto) {

        if (warehouseIncomeDto.getWarehouseName() == null || warehouseIncomeDto.getProducts() == null
                || warehouseIncomeDto.getProducts().isEmpty()) {
            throw new WarehouseIncomeValidationException("Неправильный запрос на добавление поступления продуктов!");
        }

        List<ProductIncomeDto> list = warehouseIncomeDto.getProducts().stream()
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

    public void validateProductSaleRequest(WarehouseSaleDto warehouseSaleDto) {

        if (warehouseSaleDto.getWarehouseName() == null || warehouseSaleDto.getProducts() == null
                || warehouseSaleDto.getProducts().isEmpty()) {
            throw new WarehouseIncomeValidationException("Неправильный запрос на добавление продажи продуктов!");
        }

        List<ProductSaleDto> list = warehouseSaleDto.getProducts().stream()
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

    public void validateProductTransportationRequest(WarehouseTransportationDto warehouseTransportationDto) {

        if (warehouseTransportationDto.getWarehouseNameFrom() == null
                || warehouseTransportationDto.getWarehouseNameTo() == null
                || warehouseTransportationDto.getProducts() == null
                || warehouseTransportationDto.getProducts().isEmpty()) {
            throw new WarehouseTransportationValidationException("Неправильный запрос на добавление перемещения продуктов!");
        }

        List<ProductTransportationDto> list = warehouseTransportationDto.getProducts().stream()
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

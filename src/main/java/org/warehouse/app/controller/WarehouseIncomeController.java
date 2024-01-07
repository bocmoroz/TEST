package org.warehouse.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.income.ProductIncomeDto;
import org.warehouse.app.dto.income.WarehouseIncomeBuilderDto;
import org.warehouse.app.dto.income.WarehouseIncomeDto;
import org.warehouse.app.exception.WarehouseIncomeValidationException;
import org.warehouse.app.helpers.DocumentRequestValidationService;
import org.warehouse.app.service.WarehouseIncomeService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/incomes")
@Api("Resource for income documents")
public class WarehouseIncomeController {

    private final WarehouseIncomeService warehouseIncomeService;
    private final DocumentRequestValidationService requestValidationService;

    @Autowired
    public WarehouseIncomeController(WarehouseIncomeService warehouseIncomeService,
                                     DocumentRequestValidationService requestValidationService) {
        this.warehouseIncomeService = warehouseIncomeService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    @ApiOperation("Getting all income documents")
    public ResponseDto<List<WarehouseIncomeDto>> getWarehouseIncomes() {
        List<WarehouseIncomeDto> listWarehouseIncomes = warehouseIncomeService.getWarehouseIncomes();
        return ResponseDto.success(listWarehouseIncomes);
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Getting income document by id")
    public ResponseDto<WarehouseIncomeDto> getWarehouseIncomeById(@PathVariable Long id) {
        WarehouseIncomeDto warehouseIncome = warehouseIncomeService.getWarehouseIncomeById(id);
        return ResponseDto.success(warehouseIncome);
    }

    @PostMapping
    @ApiOperation("Adding new income document")
    public ResponseDto<WarehouseIncomeDto> addNewWarehouseIncome(@RequestBody WarehouseIncomeBuilderDto warehouseIncomeBuilderDto) {
        String warehouseName = warehouseIncomeBuilderDto.getWarehouseName();
        List<ProductIncomeDto> products = warehouseIncomeBuilderDto.getProducts();
        requestValidationService.validateProductIncomeRequest(warehouseName, products);
        WarehouseIncomeDto addedWarehouseIncome = warehouseIncomeService.addNewWarehouseIncome(warehouseName, products);
        return ResponseDto.success(addedWarehouseIncome);
    }

    @ExceptionHandler(WarehouseIncomeValidationException.class)
    public ResponseDto<Object> handleWarehouseIncomeValidationException(WarehouseIncomeValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

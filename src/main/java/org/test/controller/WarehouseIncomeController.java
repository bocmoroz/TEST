package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.dto.income.ProductIncomeDto;
import org.test.dto.income.WarehouseIncomeBuilderDto;
import org.test.dto.income.WarehouseIncomeDto;
import org.test.exception.WarehouseIncomeValidationException;
import org.test.helpers.DocumentRequestValidationService;
import org.test.service.WarehouseIncomeService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/incomes")
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
    public ResponseDto<List<WarehouseIncomeDto>> getWarehouseIncomes() {
        List<WarehouseIncomeDto> listWarehouseIncomes = warehouseIncomeService.getWarehouseIncomes();
        log.info("warehouseIncomes {}", listWarehouseIncomes);
        return ResponseDto.success(listWarehouseIncomes);
    }

    @GetMapping(path = "/{id}")
    public ResponseDto<WarehouseIncomeDto> getWarehouseIncomeById(@PathVariable Long id) {
        WarehouseIncomeDto warehouseIncome = warehouseIncomeService.getWarehouseIncomeById(id);
        log.info("warehouseIncome {}", warehouseIncome);
        return ResponseDto.success(warehouseIncome);
    }

    @PostMapping
    public ResponseDto<WarehouseIncomeDto> addNewWarehouseIncome(@RequestBody WarehouseIncomeBuilderDto warehouseIncomeBuilderDto) {
        String warehouseName = warehouseIncomeBuilderDto.getWarehouseName();
        List<ProductIncomeDto> products = warehouseIncomeBuilderDto.getProducts();
        requestValidationService.validateProductIncomeRequest(warehouseName, products);
        WarehouseIncomeDto addedWarehouseIncome = warehouseIncomeService.addNewWarehouseIncome(warehouseName, products);
        log.info("addedWarehouseIncome {}", addedWarehouseIncome);
        return ResponseDto.success(addedWarehouseIncome);
    }

    @ExceptionHandler(WarehouseIncomeValidationException.class)
    public ResponseDto<Object> handleWarehouseIncomeValidationException(WarehouseIncomeValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

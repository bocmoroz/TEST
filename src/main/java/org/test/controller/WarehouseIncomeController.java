package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.dto.income.WarehouseIncomeBuilderDto;
import org.test.dto.income.WarehouseIncomeDto;
import org.test.entity.WarehouseIncome;
import org.test.exception.WarehouseIncomeValidationException;
import org.test.helpers.DocumentRequestValidationService;
import org.test.service.WarehouseIncomeService;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<ResponseDto<List<WarehouseIncomeDto>>> getWarehouseIncomes() {

        ResponseDto<List<WarehouseIncomeDto>> responseDto;

        try {
            List<WarehouseIncome> listWarehouseIncomes = warehouseIncomeService.getWarehouseIncomes();
            List<WarehouseIncomeDto> listWarehouseIncomesDto = listWarehouseIncomes.stream()
                    .map(WarehouseIncomeDto::create)
                    .collect(Collectors.toList());
            log.info("warehouseIncomes {}", listWarehouseIncomesDto);
            responseDto = new ResponseDto<>(0, "Поступления продуктов успешно получены!",
                    listWarehouseIncomesDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseIncomeValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>
                    (2, "Внутренняя ошибка, поступления продуктов не получены!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/income")
    public ResponseEntity<ResponseDto<WarehouseIncomeDto>> getWarehouseIncomeById(
            @RequestParam Long id) {

        ResponseDto<WarehouseIncomeDto> responseDto;

        try {
            WarehouseIncome warehouseIncome = warehouseIncomeService.getWarehouseIncomeById(id);
            WarehouseIncomeDto warehouseIncomeDto = WarehouseIncomeDto.create(warehouseIncome);
            log.info("warehouseIncome {}", warehouseIncomeDto);
            responseDto = new ResponseDto<>(0, "Поступление продуктов успешно получено!", warehouseIncomeDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseIncomeValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, поступление продуктов не получено!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/import")
    public ResponseEntity<ResponseDto<WarehouseIncomeDto>> addNewWarehouseIncome(
            @RequestBody WarehouseIncomeBuilderDto warehouseIncomeBuilderDto) {

        ResponseDto<WarehouseIncomeDto> responseDto;

        try {
            log.info("warehouseIncome {}", warehouseIncomeBuilderDto);
            requestValidationService.validateProductIncomeRequest(warehouseIncomeBuilderDto);
            WarehouseIncome addedWarehouseIncome = warehouseIncomeService
                    .addNewWarehouseIncome(warehouseIncomeBuilderDto.getWarehouseName(), warehouseIncomeBuilderDto.getProducts());
            WarehouseIncomeDto addedWarehouseIncomeDto = WarehouseIncomeDto.create(addedWarehouseIncome);
            log.info("addedWarehouseIncome {}", addedWarehouseIncomeDto);
            responseDto = new ResponseDto<>(0, "Поступление продуктов успешно добавлено!",
                    addedWarehouseIncomeDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseIncomeValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2,
                    "Внутренняя ошибка, поступление продуктов не добавлено!",
                    null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

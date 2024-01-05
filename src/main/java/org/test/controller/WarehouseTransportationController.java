package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.dto.transportation.ProductTransportationDto;
import org.test.dto.transportation.WarehouseTransportationBuilderDto;
import org.test.dto.transportation.WarehouseTransportationDto;
import org.test.exception.WarehouseTransportationValidationException;
import org.test.helpers.DocumentRequestValidationService;
import org.test.service.WarehouseTransportationService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/transportations")
public class WarehouseTransportationController {

    private final WarehouseTransportationService warehouseTransportationService;
    private final DocumentRequestValidationService requestValidationService;

    @Autowired
    public WarehouseTransportationController(WarehouseTransportationService warehouseTransportationService,
                                             DocumentRequestValidationService requestValidationService) {
        this.warehouseTransportationService = warehouseTransportationService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    public ResponseDto<List<WarehouseTransportationDto>> getWarehouseTransportations() {
        List<WarehouseTransportationDto> listWarehouseTransportation = warehouseTransportationService.getWarehouseTransportations();
        log.info("warehouseTransportations {}", listWarehouseTransportation);
        return ResponseDto.success(listWarehouseTransportation);
    }

    @GetMapping(path = "/{id}")
    public ResponseDto<WarehouseTransportationDto> getWarehouseTransportationById(@PathVariable Long id) {
        WarehouseTransportationDto warehouseTransportation = warehouseTransportationService.getWarehouseTransportationById(id);
        log.info("warehouseTransportation {}", warehouseTransportation);
        return ResponseDto.success(warehouseTransportation);
    }

    @PostMapping
    public ResponseDto<WarehouseTransportationDto> addNewWarehouseTransportation(
            @RequestBody WarehouseTransportationBuilderDto warehouseTransportationBuilderDto) {
        String warehouseNameFrom = warehouseTransportationBuilderDto.getWarehouseNameFrom();
        String warehouseNameTo = warehouseTransportationBuilderDto.getWarehouseNameTo();
        List<ProductTransportationDto> products = warehouseTransportationBuilderDto.getProducts();
        requestValidationService.validateProductTransportationRequest(warehouseNameFrom, warehouseNameTo, products);
        WarehouseTransportationDto addedWarehouseTransportation = warehouseTransportationService
                .addNewWarehouseTransportation(warehouseNameFrom, warehouseNameTo, products);
        log.info("addedWarehouseTransportation {}", addedWarehouseTransportation);
        return ResponseDto.success(addedWarehouseTransportation);
    }

    @ExceptionHandler(WarehouseTransportationValidationException.class)
    public ResponseDto<Object> handleWarehouseTransportationValidationException(WarehouseTransportationValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }
}

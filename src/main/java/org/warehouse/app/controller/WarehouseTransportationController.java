package org.warehouse.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.ProductTransportationDto;
import org.warehouse.app.dto.transportation.WarehouseTransportationBuilderDto;
import org.warehouse.app.dto.transportation.WarehouseTransportationDto;
import org.warehouse.app.exception.WarehouseTransportationValidationException;
import org.warehouse.app.helpers.DocumentRequestValidationService;
import org.warehouse.app.service.WarehouseTransportationService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/transportations")
@Api("Resource for transportation documents")
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
    @ApiOperation("Getting all transportation documents")
    public ResponseDto<List<WarehouseTransportationDto>> getWarehouseTransportations() {
        List<WarehouseTransportationDto> listWarehouseTransportation = warehouseTransportationService.getWarehouseTransportations();
        return ResponseDto.success(listWarehouseTransportation);
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Getting transportation document by id")
    public ResponseDto<WarehouseTransportationDto> getWarehouseTransportationById(@PathVariable Long id) {
        WarehouseTransportationDto warehouseTransportation = warehouseTransportationService.getWarehouseTransportationById(id);
        return ResponseDto.success(warehouseTransportation);
    }

    @PostMapping
    @ApiOperation("Adding new transportation document")
    public ResponseDto<WarehouseTransportationDto> addNewWarehouseTransportation(
            @RequestBody WarehouseTransportationBuilderDto warehouseTransportationBuilderDto) {
        String warehouseNameFrom = warehouseTransportationBuilderDto.getWarehouseNameFrom();
        String warehouseNameTo = warehouseTransportationBuilderDto.getWarehouseNameTo();
        List<ProductTransportationDto> products = warehouseTransportationBuilderDto.getProducts();
        requestValidationService.validateProductTransportationRequest(warehouseNameFrom, warehouseNameTo, products);
        WarehouseTransportationDto addedWarehouseTransportation = warehouseTransportationService
                .addNewWarehouseTransportation(warehouseNameFrom, warehouseNameTo, products);
        return ResponseDto.success(addedWarehouseTransportation);
    }

    @ExceptionHandler(WarehouseTransportationValidationException.class)
    public ResponseDto<Object> handleWarehouseTransportationValidationException(WarehouseTransportationValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }
}

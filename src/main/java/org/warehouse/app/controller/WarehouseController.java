package org.warehouse.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.warehouse.WarehouseBuilderDto;
import org.warehouse.app.dto.warehouse.WarehouseDto;
import org.warehouse.app.exception.WarehouseValidationException;
import org.warehouse.app.util.EntityRequestValidationService;
import org.warehouse.app.service.WarehouseService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/warehouses")
@Api("Resource for warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final EntityRequestValidationService requestValidationService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService, EntityRequestValidationService requestValidationService) {
        this.warehouseService = warehouseService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    @ApiOperation("Getting all warehouses")
    public ResponseDto<List<WarehouseDto>> getWarehouses() {
        List<WarehouseDto> listWarehouses = warehouseService.getWarehouses();
        return ResponseDto.success(listWarehouses);
    }

    @GetMapping(path = "/{name}")
    @ApiOperation("Getting warehouse by article")
    public ResponseDto<WarehouseDto> getWarehouse(@PathVariable String name) {
        WarehouseDto warehouse = warehouseService.getWarehouse(name);
        return ResponseDto.success(warehouse);
    }

    @PostMapping
    @ApiOperation("Adding new warehouse")
    public ResponseDto<WarehouseDto> addWarehouse(@RequestBody WarehouseBuilderDto warehouseBuilderDto) {
        String name = warehouseBuilderDto.getName();
        requestValidationService.validateWarehouseAddRequest(name);
        WarehouseDto addedWarehouse = warehouseService.addNewWarehouse(name);
        return ResponseDto.success(addedWarehouse);
    }

    @PutMapping(path = "/{name}")
    @ApiOperation("Updating warehouse name")
    public ResponseDto<WarehouseDto> updateWarehouse(@PathVariable("name") String oldWarehouseName,
                                                     @RequestParam String newWarehouseName) {
        requestValidationService.validateWarehouseUpdateRequest(oldWarehouseName, newWarehouseName);
        WarehouseDto updatedWarehouse = warehouseService.updateWarehouse(oldWarehouseName, newWarehouseName);
        return ResponseDto.success(updatedWarehouse);
    }

    @DeleteMapping(path = "/{name}")
    @ApiOperation("Deletion warehouse")
    public ResponseDto<WarehouseDto> deleteWarehouse(@PathVariable String name) {
        WarehouseDto deletedWarehouse = warehouseService.deleteWarehouse(name);
        return ResponseDto.success(deletedWarehouse);
    }

    @ExceptionHandler(WarehouseValidationException.class)
    public ResponseDto<Object> handleWarehouseValidationException(WarehouseValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

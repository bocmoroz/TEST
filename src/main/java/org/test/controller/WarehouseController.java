package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.dto.warehouse.WarehouseBuilderDto;
import org.test.dto.warehouse.WarehouseDto;
import org.test.exception.WarehouseValidationException;
import org.test.helpers.EntityRequestValidationService;
import org.test.service.WarehouseService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final EntityRequestValidationService requestValidationService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService, EntityRequestValidationService requestValidationService) {
        this.warehouseService = warehouseService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    public ResponseDto<List<WarehouseDto>> getWarehouses() {
        List<WarehouseDto> listWarehouses = warehouseService.getWarehouses();
        log.info("warehouses {}", listWarehouses);
        return ResponseDto.success(listWarehouses);
    }

    @GetMapping(path = "/{name}")
    public ResponseDto<WarehouseDto> getWarehouseByName(@PathVariable String name) {
        WarehouseDto warehouse = warehouseService.getWarehouse(name);
        log.info("warehouse {}", warehouse);
        return ResponseDto.success(warehouse);
    }

    @PostMapping
    public ResponseDto<WarehouseDto> addNewWarehouse(@RequestBody WarehouseBuilderDto warehouseBuilderDto) {
        String name = warehouseBuilderDto.getName();
        requestValidationService.validateWarehouseAddRequest(name);
        WarehouseDto addedWarehouse = warehouseService.addNewWarehouse(name);
        log.info("addedWarehouse {}", addedWarehouse);
        return ResponseDto.success(addedWarehouse);
    }

    @PutMapping(path = "/{name}")
    public ResponseDto<WarehouseDto> updateWarehouse(@PathVariable("name") String oldWarehouseName,
                                                     @RequestParam String newWarehouseName) {
        requestValidationService.validateWarehouseUpdateRequest(oldWarehouseName, newWarehouseName);
        WarehouseDto updatedWarehouse = warehouseService.updateWarehouse(oldWarehouseName, newWarehouseName);
        log.info("warehouse {}", updatedWarehouse);
        return ResponseDto.success(updatedWarehouse);
    }

    @DeleteMapping(path = "/{name}")
    public ResponseDto<WarehouseDto> deleteWarehouse(@PathVariable String name) {
        WarehouseDto deletedWarehouse = warehouseService.deleteWarehouse(name);
        log.info("deletedWarehouse {}", deletedWarehouse);
        return ResponseDto.success(deletedWarehouse);
    }

    @ExceptionHandler(WarehouseValidationException.class)
    public ResponseDto<Object> handleWarehouseValidationException(WarehouseValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

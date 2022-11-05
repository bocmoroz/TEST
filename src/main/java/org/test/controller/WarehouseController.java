package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.entity.Warehouse;
import org.test.exception.WarehouseValidationException;
import org.test.helpers.EntityRequestValidationService;
import org.test.service.WarehouseService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final EntityRequestValidationService entityRequestValidationService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService, EntityRequestValidationService requestValidationService) {
        this.warehouseService = warehouseService;
        this.entityRequestValidationService = requestValidationService;
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<Warehouse>>> getWarehouses() {

        ResponseDto<List<Warehouse>> responseDto;

        try {
            List<Warehouse> listWarehouses = warehouseService.getWarehouses();
            log.info("warehouses {}", listWarehouses);
            responseDto = new ResponseDto<>(0, "Склады успешно получены!", listWarehouses);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, склады не получены!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/warehouse")
    public ResponseEntity<ResponseDto<Warehouse>> getWarehouseByName(
            @RequestParam String name) {

        ResponseDto<Warehouse> responseDto;

        try {
            Warehouse warehouse = warehouseService.getWarehouseByName(name);
            log.info("warehouse {}", warehouse);
            responseDto = new ResponseDto<>(0, "Склад успешно получен!", warehouse);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, склад не получен!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/add")
    public ResponseEntity<ResponseDto<Warehouse>> addNewWarehouse(@RequestBody Warehouse warehouse) {

        ResponseDto<Warehouse> responseDto;

        try {
            log.info("warehouse {}", warehouse);
            entityRequestValidationService.validateWarehouseAddRequest(warehouse.getName());
            Warehouse addedWarehouse = warehouseService.addNewWarehouse(warehouse);
            log.info("addedWarehouse {}", addedWarehouse);
            responseDto = new ResponseDto<>(0, "Склад успешно добавлен!", addedWarehouse);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, склад не добавлен!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping(path = "/update/{warehouseName}")
    public ResponseEntity<ResponseDto<Warehouse>> updateWarehouse(
            @PathVariable("warehouseName") String warehouseName,
            @RequestParam(required = false) String newWarehouseName) {

        ResponseDto<Warehouse> responseDto;

        try {
            entityRequestValidationService.validateWarehouseUpdateRequest(warehouseName, newWarehouseName);
            Warehouse warehouse = warehouseService.updateWarehouse(warehouseName, newWarehouseName);
            log.info("warehouse {}", warehouse);
            responseDto = new ResponseDto<>(0, "Имя склада успешно обновлено!", warehouse);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, имя склада не обновлено!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<ResponseDto<Warehouse>> deleteWarehouse(
            @RequestParam(name = "deleteWarehouseName") String name) {

        ResponseDto<Warehouse> responseDto;

        try {
            Warehouse deletedWarehouse = warehouseService.deleteWarehouse(name);
            log.info("deletedWarehouse {}", deletedWarehouse);
            responseDto = new ResponseDto<>(0, "Склад успешно удалён!", deletedWarehouse);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, склад не удалён!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}

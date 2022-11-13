package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.dto.warehouse.WarehouseBuilderDto;
import org.test.dto.warehouse.WarehouseDto;
import org.test.entity.Warehouse;
import org.test.exception.WarehouseValidationException;
import org.test.helpers.EntityRequestValidationService;
import org.test.service.WarehouseService;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<ResponseDto<List<WarehouseDto>>> getWarehouses() {

        ResponseDto<List<WarehouseDto>> responseDto;

        try {
            List<Warehouse> listWarehouses = warehouseService.getWarehouses();
            List<WarehouseDto> listWarehousesDto = listWarehouses.stream()
                    .map(WarehouseDto::create)
                    .collect(Collectors.toList());
            log.info("warehouses {}", listWarehousesDto);
            responseDto = new ResponseDto<>(0, "Склады успешно получены!", listWarehousesDto);
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
    public ResponseEntity<ResponseDto<WarehouseDto>> getWarehouseByName(
            @RequestParam String name) {

        ResponseDto<WarehouseDto> responseDto;

        try {
            Warehouse warehouse = warehouseService.getWarehouseByName(name);
            WarehouseDto warehouseDto = WarehouseDto.create(warehouse);
            log.info("warehouse {}", warehouseDto);
            responseDto = new ResponseDto<>(0, "Склад успешно получен!", warehouseDto);
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
    public ResponseEntity<ResponseDto<WarehouseBuilderDto>> addNewWarehouse(
            @RequestBody WarehouseBuilderDto warehouseBuilderDto) {

        ResponseDto<WarehouseBuilderDto> responseDto;

        try {
            log.info("warehouse {}", warehouseBuilderDto);
            entityRequestValidationService.validateWarehouseAddRequest(warehouseBuilderDto.getName());
            Warehouse warehouse = new Warehouse(warehouseBuilderDto.getName());
            Warehouse addedWarehouse = warehouseService.addNewWarehouse(warehouse);
            WarehouseBuilderDto addedWarehouseBuilderDto = WarehouseBuilderDto.create(addedWarehouse);
            log.info("addedWarehouse {}", addedWarehouseBuilderDto);
            responseDto = new ResponseDto<>(0, "Склад успешно добавлен!", addedWarehouseBuilderDto);
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
    public ResponseEntity<ResponseDto<WarehouseDto>> updateWarehouse(
            @PathVariable("warehouseName") String warehouseName,
            @RequestParam(required = false) String newWarehouseName) {

        ResponseDto<WarehouseDto> responseDto;

        try {
            entityRequestValidationService.validateWarehouseUpdateRequest(warehouseName, newWarehouseName);
            Warehouse updatedWarehouse = warehouseService.updateWarehouse(warehouseName, newWarehouseName);
            WarehouseDto updatedWarehouseDto = WarehouseDto.create(updatedWarehouse);
            log.info("warehouse {}", updatedWarehouseDto);
            responseDto = new ResponseDto<>(0, "Имя склада успешно обновлено!", updatedWarehouseDto);
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
    public ResponseEntity<ResponseDto<WarehouseDto>> deleteWarehouse(
            @RequestParam(name = "deleteWarehouseName") String name) {

        ResponseDto<WarehouseDto> responseDto;

        try {
            Warehouse deletedWarehouse = warehouseService.deleteWarehouse(name);
            WarehouseDto deletedWarehouseDto = WarehouseDto.create(deletedWarehouse);
            log.info("deletedWarehouse {}", deletedWarehouseDto);
            responseDto = new ResponseDto<>(0, "Склад успешно удалён!", deletedWarehouseDto);
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

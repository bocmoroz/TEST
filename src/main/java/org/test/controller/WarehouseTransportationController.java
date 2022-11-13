package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.dto.transportation.WarehouseTransportationBuilderDto;
import org.test.dto.transportation.WarehouseTransportationDto;
import org.test.entity.WarehouseTransportation;
import org.test.exception.WarehouseTransportationValidationException;
import org.test.helpers.DocumentRequestValidationService;
import org.test.service.WarehouseTransportationService;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<ResponseDto<List<WarehouseTransportationDto>>> getWarehouseTransportations() {

        ResponseDto<List<WarehouseTransportationDto>> responseDto;

        try {
            List<WarehouseTransportation> listWarehouseTransportation =
                    warehouseTransportationService.getWarehouseTransportations();
            List<WarehouseTransportationDto> listWarehouseTransportationDto = listWarehouseTransportation.stream()
                    .map(WarehouseTransportationDto::create)
                    .collect(Collectors.toList());
            log.info("warehouseTransportations {}", listWarehouseTransportationDto);
            responseDto = new ResponseDto<>(0, "Перемещения продуктов успешно получены!",
                    listWarehouseTransportationDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseTransportationValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>
                    (2, "Внутренняя ошибка, перемещения продуктов не получены!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/transportation")
    public ResponseEntity<ResponseDto<WarehouseTransportationDto>> getWarehouseTransportationById(
            @RequestParam Long id) {

        ResponseDto<WarehouseTransportationDto> responseDto;

        try {
            WarehouseTransportation warehouseTransportation =
                    warehouseTransportationService.getWarehouseTransportationById(id);
            WarehouseTransportationDto warehouseTransportationDto =
                    WarehouseTransportationDto.create(warehouseTransportation);
            log.info("warehouseTransportation {}", warehouseTransportationDto);
            responseDto = new ResponseDto<>(0, "Перемещение продуктов успешно получено!", warehouseTransportationDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseTransportationValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, перемещение продуктов не получено!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/import")
    public ResponseEntity<ResponseDto<WarehouseTransportationDto>> addNewWarehouseTransportation(
            @RequestBody WarehouseTransportationBuilderDto warehouseTransportationBuilderDto) {

        ResponseDto<WarehouseTransportationDto> responseDto;

        try {
            log.info("warehouseTransportation {}", warehouseTransportationBuilderDto);
            requestValidationService.validateProductTransportationRequest(warehouseTransportationBuilderDto);
            WarehouseTransportation addedWarehouseTransportation = warehouseTransportationService
                    .addNewWarehouseTransportation(
                            warehouseTransportationBuilderDto.getWarehouseNameFrom(),
                            warehouseTransportationBuilderDto.getWarehouseNameTo(),
                            warehouseTransportationBuilderDto.getProducts());
            WarehouseTransportationDto addedWarehouseTransportationDto =
                    WarehouseTransportationDto.create(addedWarehouseTransportation);
            log.info("addedWarehouseTransportation {}", addedWarehouseTransportationDto);
            responseDto = new ResponseDto<>(0, "Перемещение продуктов успешно добавлено!",
                    addedWarehouseTransportationDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseTransportationValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2,
                    "Внутренняя ошибка, перемещение продуктов не добавлено!",
                    null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

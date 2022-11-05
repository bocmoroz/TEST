package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.dto.WarehouseSaleDto;
import org.test.entity.WarehouseSale;
import org.test.exception.WarehouseSaleValidationException;
import org.test.helpers.DocumentRequestValidationService;
import org.test.service.WarehouseSaleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(path = "/sales")
public class WarehouseSaleController {

    private final WarehouseSaleService warehouseSaleService;
    private final DocumentRequestValidationService requestValidationService;

    @Autowired
    public WarehouseSaleController(WarehouseSaleService warehouseSaleService, DocumentRequestValidationService requestValidationService) {
        this.warehouseSaleService = warehouseSaleService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<WarehouseSaleDto>>> getWarehouseSales() {

        ResponseDto<List<WarehouseSaleDto>> responseDto;

        try {
            List<WarehouseSale> listWarehouseSales = warehouseSaleService.getWarehouseSales();
            List<WarehouseSaleDto> listWarehouseSalesDto = listWarehouseSales.stream()
                    .map(WarehouseSaleDto::create)
                    .collect(Collectors.toList());
            log.info("warehouseSales {}", listWarehouseSalesDto);
            responseDto = new ResponseDto<>(0, "Продажи продуктов успешно получены!",
                    listWarehouseSalesDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseSaleValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>
                    (2, "Внутренняя ошибка, продажи продуктов не получены!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/sale")
    public ResponseEntity<ResponseDto<WarehouseSaleDto>> getWarehouseSaleById(
            @RequestParam Long id) {

        ResponseDto<WarehouseSaleDto> responseDto;

        try {
            WarehouseSale warehouseSale = warehouseSaleService.getWarehouseSaleById(id);
            WarehouseSaleDto warehouseSaleDto = WarehouseSaleDto.create(warehouseSale);
            log.info("warehouseSale {}", warehouseSaleDto);
            responseDto = new ResponseDto<>(0, "Продажа продуктов успешно получена!", warehouseSaleDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseSaleValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, продажа продуктов не получена!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/import")
    public ResponseEntity<ResponseDto<WarehouseSaleDto>> addNewWarehouseSale(
            @RequestBody WarehouseSaleDto warehouseSaleDto) {

        ResponseDto<WarehouseSaleDto> responseDto;

        try {
            log.info("warehouseSale {}", warehouseSaleDto);
            requestValidationService.validateProductSaleRequest(warehouseSaleDto);
            WarehouseSale addedWarehouseSale = warehouseSaleService.addNewWarehouseSale(warehouseSaleDto);
            WarehouseSaleDto addedWarehouseSaleDto = WarehouseSaleDto.create(addedWarehouseSale);
            log.info("addedWarehouseSale {}", addedWarehouseSaleDto);
            responseDto = new ResponseDto<>(0, "Продажа продуктов успешно добавлена!",
                    addedWarehouseSaleDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (WarehouseSaleValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2,
                    "Внутренняя ошибка, продажа продуктов не добавлена!",
                    null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

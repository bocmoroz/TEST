package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.dto.sale.ProductSaleDto;
import org.test.dto.sale.WarehouseSaleBuilderDto;
import org.test.dto.sale.WarehouseSaleDto;
import org.test.exception.WarehouseSaleValidationException;
import org.test.helpers.DocumentRequestValidationService;
import org.test.service.WarehouseSaleService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/sales")
public class WarehouseSaleController {

    private final WarehouseSaleService warehouseSaleService;
    private final DocumentRequestValidationService requestValidationService;

    @Autowired
    public WarehouseSaleController(WarehouseSaleService warehouseSaleService,
                                   DocumentRequestValidationService requestValidationService) {
        this.warehouseSaleService = warehouseSaleService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    public ResponseDto<List<WarehouseSaleDto>> getWarehouseSales() {
        List<WarehouseSaleDto> listWarehouseSales = warehouseSaleService.getWarehouseSales();
        log.info("warehouseSales {}", listWarehouseSales);
        return ResponseDto.success(listWarehouseSales);
    }

    @GetMapping(path = "/{id}")
    public ResponseDto<WarehouseSaleDto> getWarehouseSaleById(@PathVariable Long id) {
        WarehouseSaleDto warehouseSale = warehouseSaleService.getWarehouseSaleById(id);
        log.info("warehouseSale {}", warehouseSale);
        return ResponseDto.success(warehouseSale);
    }

    @PostMapping
    public ResponseDto<WarehouseSaleDto> addNewWarehouseSale(@RequestBody WarehouseSaleBuilderDto warehouseSaleBuilderDto) {
        String warehouseName = warehouseSaleBuilderDto.getWarehouseName();
        List<ProductSaleDto> products = warehouseSaleBuilderDto.getProducts();
        requestValidationService.validateProductSaleRequest(warehouseName, products);
        WarehouseSaleDto addedWarehouseSale = warehouseSaleService.addNewWarehouseSale(warehouseName, products);
        log.info("addedWarehouseSale {}", addedWarehouseSale);
        return ResponseDto.success(addedWarehouseSale);
    }

    @ExceptionHandler(WarehouseSaleValidationException.class)
    public ResponseDto<Object> handleWarehouseSaleValidationException(WarehouseSaleValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

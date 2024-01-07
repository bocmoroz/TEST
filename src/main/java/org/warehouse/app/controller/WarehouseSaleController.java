package org.warehouse.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.sale.ProductSaleDto;
import org.warehouse.app.dto.sale.WarehouseSaleBuilderDto;
import org.warehouse.app.dto.sale.WarehouseSaleDto;
import org.warehouse.app.exception.WarehouseSaleValidationException;
import org.warehouse.app.helpers.DocumentRequestValidationService;
import org.warehouse.app.service.WarehouseSaleService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/sales")
@Api("Resource for sale documents")
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
    @ApiOperation("Getting all sale documents")
    public ResponseDto<List<WarehouseSaleDto>> getWarehouseSales() {
        List<WarehouseSaleDto> listWarehouseSales = warehouseSaleService.getWarehouseSales();
        return ResponseDto.success(listWarehouseSales);
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Getting sale document by id")
    public ResponseDto<WarehouseSaleDto> getWarehouseSaleById(@PathVariable Long id) {
        WarehouseSaleDto warehouseSale = warehouseSaleService.getWarehouseSaleById(id);
        return ResponseDto.success(warehouseSale);
    }

    @PostMapping
    @ApiOperation("Adding new sale document")
    public ResponseDto<WarehouseSaleDto> addNewWarehouseSale(@RequestBody WarehouseSaleBuilderDto warehouseSaleBuilderDto) {
        String warehouseName = warehouseSaleBuilderDto.getWarehouseName();
        List<ProductSaleDto> products = warehouseSaleBuilderDto.getProducts();
        requestValidationService.validateProductSaleRequest(warehouseName, products);
        WarehouseSaleDto addedWarehouseSale = warehouseSaleService.addNewWarehouseSale(warehouseName, products);
        return ResponseDto.success(addedWarehouseSale);
    }

    @ExceptionHandler(WarehouseSaleValidationException.class)
    public ResponseDto<Object> handleWarehouseSaleValidationException(WarehouseSaleValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

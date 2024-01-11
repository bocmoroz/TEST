package org.warehouse.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.sale.SaleDocumentBuilderProductDto;
import org.warehouse.app.dto.sale.SaleDocumentBuilderDto;
import org.warehouse.app.dto.sale.SaleDocumentDto;
import org.warehouse.app.exception.SaleDocumentValidationException;
import org.warehouse.app.util.DocumentRequestValidationService;
import org.warehouse.app.service.SaleDocumentService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/sales")
@Api("Resource for sale documents")
public class SaleDocumentController {

    private final SaleDocumentService saleDocumentService;
    private final DocumentRequestValidationService requestValidationService;

    @Autowired
    public SaleDocumentController(SaleDocumentService saleDocumentService,
                                  DocumentRequestValidationService requestValidationService) {
        this.saleDocumentService = saleDocumentService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    @ApiOperation("Getting all sale documents")
    public ResponseDto<List<SaleDocumentDto>> getSaleDocuments() {
        List<SaleDocumentDto> listSaleDocuments = saleDocumentService.getSaleDocuments();
        return ResponseDto.success(listSaleDocuments);
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Getting sale document by id")
    public ResponseDto<SaleDocumentDto> getSaleDocumentById(@PathVariable Long id) {
        SaleDocumentDto saleDocument = saleDocumentService.getSaleDocument(id);
        return ResponseDto.success(saleDocument);
    }

    @PostMapping
    @ApiOperation("Adding new sale document")
    public ResponseDto<SaleDocumentDto> addSaleDocument(@RequestBody SaleDocumentBuilderDto saleDocumentBuilderDto) {
        String documentName = saleDocumentBuilderDto.getDocumentName();
        String warehouseName = saleDocumentBuilderDto.getWarehouseName();
        List<SaleDocumentBuilderProductDto> products = saleDocumentBuilderDto.getProducts();
        requestValidationService.validateSaleDocumentRequest(documentName, warehouseName, products);
        SaleDocumentDto addedSaleDocument = saleDocumentService.addSaleDocument(documentName, warehouseName, products);
        return ResponseDto.success(addedSaleDocument);
    }

    @ExceptionHandler(SaleDocumentValidationException.class)
    public ResponseDto<Object> handleSaleDocumentValidationException(SaleDocumentValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

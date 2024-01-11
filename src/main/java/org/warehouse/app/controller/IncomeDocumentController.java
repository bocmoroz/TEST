package org.warehouse.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.income.IncomeDocumentBuilderDto;
import org.warehouse.app.dto.income.IncomeDocumentBuilderProductDto;
import org.warehouse.app.dto.income.IncomeDocumentDto;
import org.warehouse.app.exception.IncomeDocumentValidationException;
import org.warehouse.app.util.DocumentRequestValidationService;
import org.warehouse.app.service.IncomeDocumentService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/incomes")
@Api("Resource for income documents")
public class IncomeDocumentController {

    private final IncomeDocumentService incomeDocumentService;
    private final DocumentRequestValidationService requestValidationService;

    @Autowired
    public IncomeDocumentController(IncomeDocumentService incomeDocumentService,
                                    DocumentRequestValidationService requestValidationService) {
        this.incomeDocumentService = incomeDocumentService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    @ApiOperation("Getting all income documents")
    public ResponseDto<List<IncomeDocumentDto>> getIncomeDocuments() {
        List<IncomeDocumentDto> listIncomeDocuments = incomeDocumentService.getIncomeDocuments();
        return ResponseDto.success(listIncomeDocuments);
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Getting income document by id")
    public ResponseDto<IncomeDocumentDto> getIncomeDocumentById(@PathVariable Long id) {
        IncomeDocumentDto incomeDocument = incomeDocumentService.getIncomeDocument(id);
        return ResponseDto.success(incomeDocument);
    }

    @PostMapping
    @ApiOperation("Adding new income document")
    public ResponseDto<IncomeDocumentDto> addIncomeDocument(@RequestBody IncomeDocumentBuilderDto incomeDocumentBuilderDto) {
        String documentName = incomeDocumentBuilderDto.getDocumentName();
        String warehouseName = incomeDocumentBuilderDto.getWarehouseName();
        List<IncomeDocumentBuilderProductDto> products = incomeDocumentBuilderDto.getProducts();
        requestValidationService.validateIncomeDocumentRequest(documentName, warehouseName, products);
        IncomeDocumentDto addedIncomeDocument = incomeDocumentService.addIncomeDocument(documentName, warehouseName, products);
        return ResponseDto.success(addedIncomeDocument);
    }

    @ExceptionHandler(IncomeDocumentValidationException.class)
    public ResponseDto<Object> handleIncomeDocumentValidationException(IncomeDocumentValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

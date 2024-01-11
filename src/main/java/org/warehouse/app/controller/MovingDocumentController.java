package org.warehouse.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.moving.MovingDocumentBuilderProductDto;
import org.warehouse.app.dto.moving.MovingDocumentBuilderDto;
import org.warehouse.app.dto.moving.MovingDocumentDto;
import org.warehouse.app.exception.MovingDocumentValidationException;
import org.warehouse.app.util.DocumentRequestValidationService;
import org.warehouse.app.service.MovingDocumentService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/movings")
@Api("Resource for moving documents")
public class MovingDocumentController {

    private final MovingDocumentService movingDocumentService;
    private final DocumentRequestValidationService requestValidationService;

    @Autowired
    public MovingDocumentController(MovingDocumentService movingDocumentService,
                                    DocumentRequestValidationService requestValidationService) {
        this.movingDocumentService = movingDocumentService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    @ApiOperation("Getting all moving documents")
    public ResponseDto<List<MovingDocumentDto>> getMovingDocuments() {
        List<MovingDocumentDto> listMovingDocuments = movingDocumentService.getMovingDocuments();
        return ResponseDto.success(listMovingDocuments);
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Getting moving document by id")
    public ResponseDto<MovingDocumentDto> getMovingDocumentById(@PathVariable Long id) {
        MovingDocumentDto movingDocument = movingDocumentService.getMovingDocument(id);
        return ResponseDto.success(movingDocument);
    }

    @PostMapping
    @ApiOperation("Adding new moving document")
    public ResponseDto<MovingDocumentDto> addMovingDocument(@RequestBody MovingDocumentBuilderDto movingDocumentBuilderDto) {
        String documentName = movingDocumentBuilderDto.getDocumentName();
        String warehouseNameFrom = movingDocumentBuilderDto.getWarehouseNameFrom();
        String warehouseNameTo = movingDocumentBuilderDto.getWarehouseNameTo();
        List<MovingDocumentBuilderProductDto> products = movingDocumentBuilderDto.getProducts();
        requestValidationService.validateMovingDocumentRequest(documentName, warehouseNameFrom, warehouseNameTo, products);
        MovingDocumentDto addedMovingDocument = movingDocumentService
                .addMovingDocument(documentName, warehouseNameFrom, warehouseNameTo, products);
        return ResponseDto.success(addedMovingDocument);
    }

    @ExceptionHandler(MovingDocumentValidationException.class)
    public ResponseDto<Object> handleMovingDocumentValidationException(MovingDocumentValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }
}

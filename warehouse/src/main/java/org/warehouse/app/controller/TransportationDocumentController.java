package org.warehouse.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.warehouse.app.dto.DocumentDto;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.transportation.TransportationDocumentBuilderDto;
import org.warehouse.app.enums.TransportationDocumentTypeEnum;
import org.warehouse.app.exception.TransportationDocumentValidationException;
import org.warehouse.app.service.TransportationDocumentService;
import org.warehouse.app.util.DocumentRequestValidationService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/transportations")
@Api("Resource for transportation documents")
public class TransportationDocumentController {

    private final TransportationDocumentService transportationDocumentService;
    private final DocumentRequestValidationService requestValidationService;

    @Autowired
    public TransportationDocumentController(TransportationDocumentService transportationDocumentService,
                                            DocumentRequestValidationService requestValidationService) {
        this.transportationDocumentService = transportationDocumentService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    @ApiOperation("Getting all transportation documents")
    public ResponseDto<List<DocumentDto>> getTransportationDocuments() {
        List<DocumentDto> listTransportationDocuments = transportationDocumentService.getTransportationDocuments();
        return ResponseDto.success(listTransportationDocuments);
    }

    @GetMapping(params = "type")
    @ApiOperation("Getting all transportation documents by type")
    public ResponseDto<List<DocumentDto>> getTransportationDocumentsByType(@RequestParam String type) {
        List<DocumentDto> listTransportationDocuments = transportationDocumentService
                .getTransportationDocumentsByType(TransportationDocumentTypeEnum.valueOf(type));
        return ResponseDto.success(listTransportationDocuments);
    }

    @GetMapping(path = "/{id}")
    @ApiOperation("Getting transportation document by id")
    public ResponseDto<DocumentDto> getTransportationDocumentById(@PathVariable Long id) {
        DocumentDto transportationDocument = transportationDocumentService.getTransportationDocument(id);
        return ResponseDto.success(transportationDocument);
    }

    @PostMapping
    @ApiOperation("Adding new transportation document")
    public ResponseDto<DocumentDto> addTransportationDocument(@RequestBody TransportationDocumentBuilderDto transportationDocumentBuilderDto) {
        requestValidationService.validateTransportationDocumentRequest(transportationDocumentBuilderDto);
        DocumentDto addedTransportationDocument = transportationDocumentService.addTransportationDocument(transportationDocumentBuilderDto);
        return ResponseDto.success(addedTransportationDocument);
    }

    @ExceptionHandler(TransportationDocumentValidationException.class)
    public ResponseDto<Object> handleTransportationDocumentValidationException(TransportationDocumentValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

package org.warehouse.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.warehouse.app.dto.DocumentDto;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.transportation.TransportationDocumentBuilderDto;
import org.warehouse.app.dto.transportation.TransportationDocumentDto;
import org.warehouse.app.enums.TransportationDocumentTypeEnum;
import org.warehouse.app.exception.TransportationDocumentValidationException;
import org.warehouse.app.model.TransportationDocumentEntity;
import org.warehouse.app.service.TransportationDocumentService;
import org.warehouse.app.util.DocumentRequestValidationService;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.warehouse.app.dto.ResponseDto.StatusEnum.*;
import static org.warehouse.app.enums.TransportationDocumentTypeEnum.INCOME;

@SpringBootTest(classes = {TransportationDocumentController.class})
@RunWith(SpringRunner.class)
public class TransportationDocumentControllerTest {

    private static final Long DEFAULT_ID = 999L;
    private static final String DEFAULT_NAME = "Name12345";
    private static final TransportationDocumentTypeEnum DEFAULT_TYPE = INCOME;

    @Autowired
    private TransportationDocumentController transportationDocumentController;

    @MockBean
    private TransportationDocumentService transportationDocumentService;

    @MockBean
    private DocumentRequestValidationService requestValidationService;

    private MockMvc mockMvc;

    @PostConstruct
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(transportationDocumentController)
                .setControllerAdvice(new CommonExceptionHandlers())
                .build();
    }

    @Test
    public void getTransportationDocumentsTestSuccess() {
        Mockito.when(transportationDocumentService.getTransportationDocuments())
                .thenReturn(Collections.singletonList(Mockito.mock(TransportationDocumentDto.class)));

        ResponseDto<List<DocumentDto>> response = transportationDocumentController.getTransportationDocuments();

        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().size());
        Assert.assertNull(response.getMessage());

        Mockito.verify(transportationDocumentService, Mockito.times(1)).getTransportationDocuments();
    }

    @Test
    public void getTransportationDocumentsTestFailInternalError() throws Exception {
        String errorMessage = "Internal server error";
        Mockito.when(transportationDocumentService.getTransportationDocuments()).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(get("/transportations"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(transportationDocumentService, Mockito.times(1)).getTransportationDocuments();
    }

    @Test
    public void getTransportationDocumentByIdTestSuccess() {
        DocumentDto document = new TransportationDocumentDto(Mockito.mock(TransportationDocumentEntity.class));
        DocumentDto spyDocument = Mockito.spy(document);
        Mockito.when(spyDocument.getName()).thenReturn(DEFAULT_NAME);

        Mockito.when(transportationDocumentService.getTransportationDocument(Mockito.anyLong())).thenReturn(spyDocument);

        ResponseDto<DocumentDto> response = transportationDocumentController.getTransportationDocumentById(Mockito.anyLong());

        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(response.getBody(), spyDocument);
        Assert.assertEquals(response.getBody().getName(), spyDocument.getName());
        Assert.assertNull(response.getMessage());

        Mockito.verify(transportationDocumentService, Mockito.times(1)).getTransportationDocument(Mockito.anyLong());
    }

    @Test
    public void getTransportationDocumentByIdTestFailNotFound() throws Exception {
        String errorMessage = String.format("There is no document with id %s", DEFAULT_ID);
        Mockito.when(transportationDocumentService.getTransportationDocument(DEFAULT_ID)).thenThrow(
                new TransportationDocumentValidationException(errorMessage));

        mockMvc.perform(get("/transportations/{id}", DEFAULT_ID))
                .andExpect(jsonPath("$.status").value(FAIL.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(transportationDocumentService, Mockito.times(1)).getTransportationDocument(Mockito.anyLong());
    }

    @Test
    public void getTransportationDocumentByIdTestFailInternalError() throws Exception {
        String errorMessage = "Internal server error";
        Mockito.when(transportationDocumentService.getTransportationDocument(DEFAULT_ID)).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(get("/transportations/{id}", DEFAULT_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(transportationDocumentService, Mockito.times(1)).getTransportationDocument(Mockito.anyLong());
    }

    @Test
    public void getTransportationDocumentsByTypeTestSuccess() {
        DocumentDto document = new TransportationDocumentDto(Mockito.mock(TransportationDocumentEntity.class));
        DocumentDto spyDocument = Mockito.spy(document);
        Mockito.when(spyDocument.getName()).thenReturn(DEFAULT_NAME);
        Mockito.when(spyDocument.getType()).thenReturn(DEFAULT_TYPE);

        Mockito.when(transportationDocumentService.getTransportationDocumentsByType(Mockito.any(TransportationDocumentTypeEnum.class)))
                .thenReturn(Collections.singletonList(spyDocument));

        ResponseDto<List<DocumentDto>> response = transportationDocumentController.getTransportationDocumentsByType(DEFAULT_TYPE.toString());

        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(response.getBody(), Collections.singletonList(spyDocument));
        Assert.assertEquals(response.getBody().get(0).getName(), spyDocument.getName());
        Assert.assertEquals(response.getBody().get(0).getType(), spyDocument.getType());
        Assert.assertNull(response.getMessage());

        Mockito.verify(transportationDocumentService, Mockito.times(1))
                .getTransportationDocumentsByType(Mockito.any(TransportationDocumentTypeEnum.class));
    }

    @Test
    public void getTransportationDocumentsByTypeTestFailInternalError() throws Exception {
        String errorMessage = "Internal server error";
        Mockito.when(transportationDocumentService.getTransportationDocumentsByType(DEFAULT_TYPE))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(get("/transportations")
                .param("type", DEFAULT_TYPE.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(transportationDocumentService, Mockito.times(1))
                .getTransportationDocumentsByType(Mockito.any(TransportationDocumentTypeEnum.class));
    }

    @Test
    public void addTransportationDocumentTestSuccess() {
        TransportationDocumentBuilderDto documentBuilderDto = new TransportationDocumentBuilderDto();
        documentBuilderDto.setDocumentName(DEFAULT_NAME);

        DocumentDto document = new TransportationDocumentDto(Mockito.mock(TransportationDocumentEntity.class));
        DocumentDto spyDocument = Mockito.spy(document);
        Mockito.when(spyDocument.getName()).thenReturn(DEFAULT_NAME);

        Mockito.when(transportationDocumentService.addTransportationDocument(documentBuilderDto)).thenReturn(document);

        ResponseDto<DocumentDto> response = transportationDocumentController.addTransportationDocument(documentBuilderDto);

        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(response.getBody(), document);
        Assert.assertEquals(response.getBody().getName(), document.getName());
        Assert.assertNull(response.getMessage());

        Mockito.verify(transportationDocumentService, Mockito.times(1))
                .addTransportationDocument(Mockito.any(TransportationDocumentBuilderDto.class));
    }

    @Test
    public void addTransportationDocumentTestFailWarehouseNotExist() throws Exception {
        TransportationDocumentBuilderDto documentBuilderDto = new TransportationDocumentBuilderDto();
        documentBuilderDto.setDocumentName(DEFAULT_NAME);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(documentBuilderDto);

        String errorMessage = String.format("Document with name %s already exists", DEFAULT_NAME);
        Mockito.when(transportationDocumentService.addTransportationDocument(Mockito.any(TransportationDocumentBuilderDto.class)))
                .thenThrow(new TransportationDocumentValidationException(errorMessage));

        mockMvc.perform(post("/transportations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(jsonPath("$.status").value(FAIL.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(transportationDocumentService, Mockito.times(1))
                .addTransportationDocument(Mockito.any(TransportationDocumentBuilderDto.class));
    }

    @Test
    public void addTransportationDocumentTestFailInternalError() throws Exception {
        TransportationDocumentBuilderDto documentBuilderDto = new TransportationDocumentBuilderDto();
        documentBuilderDto.setDocumentName(DEFAULT_NAME);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(documentBuilderDto);

        String errorMessage = "Internal server error";
        Mockito.when(transportationDocumentService.addTransportationDocument(Mockito.any(TransportationDocumentBuilderDto.class)))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/transportations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(transportationDocumentService, Mockito.times(1))
                .addTransportationDocument(Mockito.any(TransportationDocumentBuilderDto.class));
    }

}
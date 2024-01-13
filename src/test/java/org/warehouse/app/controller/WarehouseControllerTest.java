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
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.warehouse.WarehouseBuilderDto;
import org.warehouse.app.dto.warehouse.WarehouseDto;
import org.warehouse.app.exception.WarehouseValidationException;
import org.warehouse.app.service.WarehouseService;
import org.warehouse.app.util.EntityRequestValidationService;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.warehouse.app.dto.ResponseDto.StatusEnum.*;

@SpringBootTest(classes = {WarehouseController.class})
@RunWith(SpringRunner.class)
public class WarehouseControllerTest {

    private static final String DEFAULT_NAME = "Name12345";

    @Autowired
    private WarehouseController warehouseController;

    @MockBean
    private WarehouseService warehouseService;

    @MockBean
    private EntityRequestValidationService entityRequestValidationService;

    private MockMvc mockMvc;

    @PostConstruct
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(warehouseController)
                .setControllerAdvice(new CommonExceptionHandlers())
                .build();
    }

    @Test
    public void getWarehousesTestSuccess() {
        Mockito.when(warehouseService.getWarehouses())
                .thenReturn(Collections.singletonList(Mockito.mock(WarehouseDto.class)));

        ResponseDto<List<WarehouseDto>> response = warehouseController.getWarehouses();

        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().size());
        Assert.assertNull(response.getMessage());

        Mockito.verify(warehouseService, Mockito.times(1)).getWarehouses();
    }

    @Test
    public void getWarehousesTestFailInternalError() throws Exception {
        String errorMessage = "Internal server error";
        Mockito.when(warehouseService.getWarehouses()).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(get("/warehouses"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(warehouseService, Mockito.times(1)).getWarehouses();
    }

    @Test
    public void getWarehouseByNameTestSuccess() {
        WarehouseDto warehouse = new WarehouseDto();
        warehouse.setName(DEFAULT_NAME);

        Mockito.when(warehouseService.getWarehouse(Mockito.anyString())).thenReturn(warehouse);

        ResponseDto<WarehouseDto> response = warehouseController.getWarehouse(Mockito.anyString());

        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(response.getBody(), warehouse);
        Assert.assertEquals(response.getBody().getName(), warehouse.getName());
        Assert.assertNull(response.getMessage());

        Mockito.verify(warehouseService, Mockito.times(1)).getWarehouse(Mockito.anyString());
    }

    @Test
    public void getWarehouseByNameTestFailNotFound() throws Exception {
        String errorMessage = String.format("There is no warehouse with name %s", DEFAULT_NAME);
        Mockito.when(warehouseService.getWarehouse(DEFAULT_NAME)).thenThrow(
                new WarehouseValidationException(errorMessage));

        mockMvc.perform(get("/warehouses/{name}", DEFAULT_NAME))
                .andExpect(jsonPath("$.status").value(FAIL.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(warehouseService, Mockito.times(1)).getWarehouse(Mockito.anyString());
    }

    @Test
    public void getWarehouseByNameTestFailInternalError() throws Exception {
        String errorMessage = "Internal server error";
        Mockito.when(warehouseService.getWarehouse(DEFAULT_NAME)).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(get("/warehouses/{name}", DEFAULT_NAME))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(warehouseService, Mockito.times(1)).getWarehouse(Mockito.anyString());
    }

    @Test
    public void addNewWarehouseTestSuccess() {
        WarehouseBuilderDto warehouseBuilderDto = new WarehouseBuilderDto();
        warehouseBuilderDto.setName(DEFAULT_NAME);

        WarehouseDto warehouse = new WarehouseDto();
        warehouse.setName(DEFAULT_NAME);

        Mockito.when(warehouseService.addNewWarehouse(DEFAULT_NAME)).thenReturn(warehouse);

        ResponseDto<WarehouseDto> response = warehouseController.addWarehouse(warehouseBuilderDto);

        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(response.getBody(), warehouse);
        Assert.assertEquals(response.getBody().getName(), warehouse.getName());
        Assert.assertNull(response.getMessage());

        Mockito.verify(warehouseService, Mockito.times(1)).addNewWarehouse(Mockito.anyString());
    }

    @Test
    public void addNewWarehouseTestFailAlreadyExist() throws Exception {
        WarehouseBuilderDto warehouseBuilderDto = new WarehouseBuilderDto();
        warehouseBuilderDto.setName(DEFAULT_NAME);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(warehouseBuilderDto);

        String errorMessage = String.format("Article %s already exists", DEFAULT_NAME);
        Mockito.when(warehouseService.addNewWarehouse(Mockito.anyString()))
                .thenThrow(new WarehouseValidationException(errorMessage));

        mockMvc.perform(post("/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(jsonPath("$.status").value(FAIL.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(warehouseService, Mockito.times(1)).addNewWarehouse(Mockito.anyString());
    }

    @Test
    public void addNewWarehouseTestFailInternalError() throws Exception {
        WarehouseBuilderDto warehouseBuilderDto = new WarehouseBuilderDto();
        warehouseBuilderDto.setName(DEFAULT_NAME);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(warehouseBuilderDto);

        String errorMessage = "Internal server error";
        Mockito.when(warehouseService.addNewWarehouse(Mockito.anyString()))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(warehouseService, Mockito.times(1)).addNewWarehouse(Mockito.anyString());

    }

}

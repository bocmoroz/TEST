package org.test.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.test.dto.ResponseDto;
import org.test.dto.WarehouseIncomeDto;
import org.test.entity.Warehouse;
import org.test.entity.WarehouseIncome;
import org.test.exception.WarehouseIncomeValidationException;
import org.test.helpers.DocumentRequestValidationService;
import org.test.service.WarehouseIncomeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;

@SpringBootTest(classes = {WarehouseIncomeController.class})
@RunWith(SpringRunner.class)
public class WarehouseIncomeControllerTest {

    @Autowired
    private WarehouseIncomeController warehouseIncomeController;

    @MockBean
    private WarehouseIncomeService warehouseIncomeService;

    @MockBean
    private DocumentRequestValidationService requestValidationService;

    @Test
    public void getWarehouseIncomesTestSuccess() {
        WarehouseIncome warehouseIncome = new WarehouseIncome();
        warehouseIncome.setWarehouse(new Warehouse());
        warehouseIncome.setProducts(new ArrayList<>());

        Mockito.when(warehouseIncomeService.getWarehouseIncomes())
                .thenReturn(Collections.singletonList(warehouseIncome));

        ResponseEntity<ResponseDto<List<WarehouseIncomeDto>>> response =
                warehouseIncomeController.getWarehouseIncomes();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Поступления продуктов успешно получены!", response.getBody().getMessage());
        Assert.assertEquals(1, response.getBody().getBody().size());
    }

    @Test
    public void getWarehouseIncomesTestFailNotFound() {
        Mockito.when(warehouseIncomeService.getWarehouseIncomes())
                .thenThrow(new WarehouseIncomeValidationException("В БД нет поступлений продуктов!"));

        ResponseEntity<ResponseDto<List<WarehouseIncomeDto>>> response = warehouseIncomeController.getWarehouseIncomes();

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("В БД нет поступлений продуктов!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getWarehouseIncomesTestFailInternalError() {
        Mockito.when(warehouseIncomeService.getWarehouseIncomes()).thenThrow(new RuntimeException());

        ResponseEntity<ResponseDto<List<WarehouseIncomeDto>>> response = warehouseIncomeController.getWarehouseIncomes();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, поступления продуктов не получены!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getWarehouseIncomeByIdTestSuccess() {
        WarehouseIncome warehouseIncome = new WarehouseIncome();
        warehouseIncome.setWarehouse(new Warehouse());
        warehouseIncome.setProducts(new ArrayList<>());
        WarehouseIncomeDto warehouseIncomeDto = WarehouseIncomeDto.create(warehouseIncome);

        Mockito.when(warehouseIncomeService.getWarehouseIncomeById(warehouseIncome.getId())).thenReturn(warehouseIncome);

        ResponseEntity<ResponseDto<WarehouseIncomeDto>> response =
                warehouseIncomeController.getWarehouseIncomeById(warehouseIncome.getId());

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Поступление продуктов успешно получено!", response.getBody().getMessage());
        Assert.assertEquals(response.getBody().getBody(), warehouseIncomeDto);
    }

    @Test
    public void getWarehouseIncomeByIdTestFailNotFound() {
        WarehouseIncome warehouseIncome = new WarehouseIncome();
        warehouseIncome.setWarehouse(new Warehouse());
        warehouseIncome.setProducts(new ArrayList<>());

        Mockito.when(warehouseIncomeService.getWarehouseIncomeById(warehouseIncome.getId())).thenThrow(
                new WarehouseIncomeValidationException("Поступления с номером " + warehouseIncome.getId() + " не существует!"));

        ResponseEntity<ResponseDto<WarehouseIncomeDto>> response =
                warehouseIncomeController.getWarehouseIncomeById(warehouseIncome.getId());

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("Поступления с номером " + warehouseIncome.getId() + " не существует!",
                response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getWarehouseIncomeByIdTestFailInternalError() {
        WarehouseIncome warehouseIncome = new WarehouseIncome();
        warehouseIncome.setWarehouse(new Warehouse());
        warehouseIncome.setProducts(new ArrayList<>());

        Mockito.when(warehouseIncomeService.getWarehouseIncomeById(warehouseIncome.getId()))
                .thenThrow(new RuntimeException("Внутренняя ошибка"));

        ResponseEntity<ResponseDto<WarehouseIncomeDto>> response =
                warehouseIncomeController.getWarehouseIncomeById(warehouseIncome.getId());

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, поступление продуктов не получено!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void addNewWarehouseIncomeTestSuccess() {
        WarehouseIncome warehouseIncome = new WarehouseIncome();
        warehouseIncome.setWarehouse(new Warehouse());
        warehouseIncome.setProducts(new ArrayList<>());
        WarehouseIncomeDto warehouseIncomeDto = WarehouseIncomeDto.create(warehouseIncome);


        doNothing().when(requestValidationService).validateProductIncomeRequest(warehouseIncomeDto);
        Mockito.when(warehouseIncomeService.addNewWarehouseIncome(warehouseIncomeDto)).thenReturn(warehouseIncome);

        ResponseEntity<ResponseDto<WarehouseIncomeDto>> response =
                warehouseIncomeController.addNewWarehouseIncome(warehouseIncomeDto);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Поступление продуктов успешно добавлено!", response.getBody().getMessage());
        Assert.assertEquals(response.getBody().getBody(), warehouseIncomeDto);
    }

    @Test
    public void addNewWarehouseIncomeTestFailWarehouseNotExist() {
        WarehouseIncome warehouseIncome = new WarehouseIncome();
        warehouseIncome.setWarehouse(new Warehouse());
        warehouseIncome.setProducts(new ArrayList<>());
        WarehouseIncomeDto warehouseIncomeDto = WarehouseIncomeDto.create(warehouseIncome);

        doNothing().when(requestValidationService).validateProductIncomeRequest(warehouseIncomeDto);
        Mockito.when(warehouseIncomeService.addNewWarehouseIncome(warehouseIncomeDto))
                .thenThrow(new WarehouseIncomeValidationException(
                        "Склада с именем "
                                + warehouseIncomeDto.getWarehouseName()
                                + " не существует!"));

        ResponseEntity<ResponseDto<WarehouseIncomeDto>> response =
                warehouseIncomeController.addNewWarehouseIncome(warehouseIncomeDto);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("Склада с именем " + warehouseIncomeDto.getWarehouseName() + " не существует!",
                response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());

    }

    @Test
    public void addNewWarehouseIncomeTestFailInternalError() {
        WarehouseIncome warehouseIncome = new WarehouseIncome();
        warehouseIncome.setWarehouse(new Warehouse());
        warehouseIncome.setProducts(new ArrayList<>());
        WarehouseIncomeDto warehouseIncomeDto = WarehouseIncomeDto.create(warehouseIncome);

        doNothing().when(requestValidationService).validateProductIncomeRequest(warehouseIncomeDto);
        Mockito.when(warehouseIncomeService.addNewWarehouseIncome(warehouseIncomeDto))
                .thenThrow(new RuntimeException("Внутренняя ошибка"));

        ResponseEntity<ResponseDto<WarehouseIncomeDto>> response =
                warehouseIncomeController.addNewWarehouseIncome(warehouseIncomeDto);

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, поступление продуктов не добавлено!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());

    }

}
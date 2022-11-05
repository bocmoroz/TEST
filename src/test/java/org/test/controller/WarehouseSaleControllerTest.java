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
import org.test.dto.WarehouseSaleDto;
import org.test.entity.Warehouse;
import org.test.entity.WarehouseSale;
import org.test.exception.WarehouseSaleValidationException;
import org.test.helpers.DocumentRequestValidationService;
import org.test.service.WarehouseSaleService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;

@SpringBootTest(classes = {WarehouseSaleController.class})
@RunWith(SpringRunner.class)
public class WarehouseSaleControllerTest {

    @Autowired
    private WarehouseSaleController warehouseSaleController;

    @MockBean
    private WarehouseSaleService warehouseSaleService;

    @MockBean
    private DocumentRequestValidationService requestValidationService;

    @Test
    public void getWarehouseSalesTestSuccess() {
        WarehouseSale warehouseSale = new WarehouseSale();
        warehouseSale.setWarehouse(new Warehouse());
        warehouseSale.setProducts(new ArrayList<>());

        Mockito.when(warehouseSaleService.getWarehouseSales())
                .thenReturn(Collections.singletonList(warehouseSale));

        ResponseEntity<ResponseDto<List<WarehouseSaleDto>>> response =
                warehouseSaleController.getWarehouseSales();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Продажи продуктов успешно получены!", response.getBody().getMessage());
        Assert.assertEquals(1, response.getBody().getBody().size());
    }

    @Test
    public void getWarehouseSalesTestFailNotFound() {
        Mockito.when(warehouseSaleService.getWarehouseSales())
                .thenThrow(new WarehouseSaleValidationException("В БД нет продажи продуктов!"));

        ResponseEntity<ResponseDto<List<WarehouseSaleDto>>> response = warehouseSaleController.getWarehouseSales();

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("В БД нет продажи продуктов!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getWarehouseSalesTestFailInternalError() {
        Mockito.when(warehouseSaleService.getWarehouseSales()).thenThrow(new RuntimeException());

        ResponseEntity<ResponseDto<List<WarehouseSaleDto>>> response = warehouseSaleController.getWarehouseSales();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, продажи продуктов не получены!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getWarehouseSaleByIdTestSuccess() {
        WarehouseSale warehouseSale = new WarehouseSale();
        warehouseSale.setWarehouse(new Warehouse());
        warehouseSale.setProducts(new ArrayList<>());
        WarehouseSaleDto warehouseSaleDto = WarehouseSaleDto.create(warehouseSale);

        Mockito.when(warehouseSaleService.getWarehouseSaleById(warehouseSale.getId())).thenReturn(warehouseSale);

        ResponseEntity<ResponseDto<WarehouseSaleDto>> response =
                warehouseSaleController.getWarehouseSaleById(warehouseSale.getId());

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Продажа продуктов успешно получена!", response.getBody().getMessage());
        Assert.assertEquals(response.getBody().getBody(), warehouseSaleDto);
    }

    @Test
    public void getWarehouseSaleByIdTestFailNotFound() {
        WarehouseSale warehouseSale = new WarehouseSale();
        warehouseSale.setWarehouse(new Warehouse());
        warehouseSale.setProducts(new ArrayList<>());

        Mockito.when(warehouseSaleService.getWarehouseSaleById(warehouseSale.getId())).thenThrow(
                new WarehouseSaleValidationException("Продажи с номером " + warehouseSale.getId() + " не существует!"));

        ResponseEntity<ResponseDto<WarehouseSaleDto>> response =
                warehouseSaleController.getWarehouseSaleById(warehouseSale.getId());

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("Продажи с номером " + warehouseSale.getId() + " не существует!",
                response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getWarehouseSaleByIdTestFailInternalError() {
        WarehouseSale warehouseSale = new WarehouseSale();
        warehouseSale.setWarehouse(new Warehouse());
        warehouseSale.setProducts(new ArrayList<>());

        Mockito.when(warehouseSaleService.getWarehouseSaleById(warehouseSale.getId()))
                .thenThrow(new RuntimeException("Внутренняя ошибка"));

        ResponseEntity<ResponseDto<WarehouseSaleDto>> response =
                warehouseSaleController.getWarehouseSaleById(warehouseSale.getId());

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, продажа продуктов не получена!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void addNewWarehouseSaleTestSuccess() {
        WarehouseSale warehouseSale = new WarehouseSale();
        warehouseSale.setWarehouse(new Warehouse());
        warehouseSale.setProducts(new ArrayList<>());
        WarehouseSaleDto warehouseSaleDto = WarehouseSaleDto.create(warehouseSale);

        doNothing().when(requestValidationService).validateProductSaleRequest(warehouseSaleDto);
        Mockito.when(warehouseSaleService.addNewWarehouseSale(warehouseSaleDto)).thenReturn(warehouseSale);

        ResponseEntity<ResponseDto<WarehouseSaleDto>> response =
                warehouseSaleController.addNewWarehouseSale(warehouseSaleDto);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Продажа продуктов успешно добавлена!", response.getBody().getMessage());
        Assert.assertEquals(response.getBody().getBody(), warehouseSaleDto);
    }

    @Test
    public void addNewWarehouseSaleTestFailWarehouseNotExist() {
        WarehouseSale warehouseSale = new WarehouseSale();
        warehouseSale.setWarehouse(new Warehouse());
        warehouseSale.setProducts(new ArrayList<>());
        WarehouseSaleDto warehouseSaleDto = WarehouseSaleDto.create(warehouseSale);

        doNothing().when(requestValidationService).validateProductSaleRequest(warehouseSaleDto);
        Mockito.when(warehouseSaleService.addNewWarehouseSale(warehouseSaleDto))
                .thenThrow(new WarehouseSaleValidationException(
                        "Склада с именем "
                                + warehouseSaleDto.getWarehouseName()
                                + " не существует!"));

        ResponseEntity<ResponseDto<WarehouseSaleDto>> response =
                warehouseSaleController.addNewWarehouseSale(warehouseSaleDto);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("Склада с именем " + warehouseSaleDto.getWarehouseName() + " не существует!",
                response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());

    }

    @Test
    public void addNewWarehouseSaleTestFailInternalError() {
        WarehouseSale warehouseSale = new WarehouseSale();
        warehouseSale.setWarehouse(new Warehouse());
        warehouseSale.setProducts(new ArrayList<>());
        WarehouseSaleDto warehouseSaleDto = WarehouseSaleDto.create(warehouseSale);

        doNothing().when(requestValidationService).validateProductSaleRequest(warehouseSaleDto);
        Mockito.when(warehouseSaleService.addNewWarehouseSale(warehouseSaleDto))
                .thenThrow(new RuntimeException("Внутренняя ошибка"));

        ResponseEntity<ResponseDto<WarehouseSaleDto>> response =
                warehouseSaleController.addNewWarehouseSale(warehouseSaleDto);

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, продажа продуктов не добавлена!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());

    }

}
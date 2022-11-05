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
import org.test.entity.Product;
import org.test.entity.Warehouse;
import org.test.exception.ProductValidationException;
import org.test.exception.WarehouseValidationException;
import org.test.helpers.EntityRequestValidationService;
import org.test.service.WarehouseService;

import java.util.Collections;
import java.util.List;

@SpringBootTest(classes = {WarehouseController.class, EntityRequestValidationService.class})
@RunWith(SpringRunner.class)
public class WarehouseControllerTest {

    @Autowired
    private WarehouseController warehouseController;

    @MockBean
    private WarehouseService warehouseService;

    @Test
    public void getWarehousesTestSuccess() {
        Mockito.when(warehouseService.getWarehouses()).thenReturn(Collections.singletonList(new Warehouse()));

        ResponseEntity<ResponseDto<List<Warehouse>>> response = warehouseController.getWarehouses();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Склады успешно получены!", response.getBody().getMessage());
        Assert.assertEquals(1, response.getBody().getBody().size());
    }

    @Test
    public void getWarehousesTestFailNotFound() {
        Mockito.when(warehouseService.getWarehouses())
                .thenThrow(new WarehouseValidationException("В БД нет складов!"));

        ResponseEntity<ResponseDto<List<Warehouse>>> response = warehouseController.getWarehouses();

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("В БД нет складов!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getWarehousesTestFailInternalError() {
        Mockito.when(warehouseService.getWarehouses()).thenThrow(new RuntimeException());

        ResponseEntity<ResponseDto<List<Warehouse>>> response = warehouseController.getWarehouses();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, склады не получены!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getWarehouseByNameTestSuccess() {
        Warehouse warehouse = new Warehouse();
        warehouse.setName("имя");
        Mockito.when(warehouseService.getWarehouseByName(warehouse.getName())).thenReturn(warehouse);

        ResponseEntity<ResponseDto<Warehouse>> response = warehouseController.getWarehouseByName(warehouse.getName());

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Склад успешно получен!", response.getBody().getMessage());
        Assert.assertEquals(response.getBody().getBody(), warehouse);
    }

    @Test
    public void getWarehouseByNameTestFailNotFound() {
        Warehouse warehouse = new Warehouse();
        warehouse.setName("имя");
        Mockito.when(warehouseService.getWarehouseByName(warehouse.getName())).thenThrow(
                new WarehouseValidationException("Склад с именем  " + warehouse.getName() + " не существует!"));

        ResponseEntity<ResponseDto<Warehouse>> response = warehouseController.getWarehouseByName(warehouse.getName());

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("Склад с именем  " + warehouse.getName() 
                + " не существует!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getWarehouseByNameTestFailInternalError() {
        Warehouse warehouse = new Warehouse();
        warehouse.setName("имя");
        Mockito.when(warehouseService.getWarehouseByName(warehouse.getName()))
                .thenThrow(new RuntimeException("Внутренняя ошибка"));

        ResponseEntity<ResponseDto<Warehouse>> response = warehouseController.getWarehouseByName(warehouse.getName());

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, склад не получен!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void addNewWarehouseTestSuccess(){
        Warehouse warehouse = new Warehouse();
        warehouse.setName("имя");
        Mockito.when(warehouseService.addNewWarehouse(warehouse)).thenReturn(warehouse);

        ResponseEntity<ResponseDto<Warehouse>> response = warehouseController.addNewWarehouse(warehouse);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Склад успешно добавлен!", response.getBody().getMessage());
        Assert.assertEquals(response.getBody().getBody(), warehouse);
    }

    @Test
    public void addNewWarehouseTestFailAlreadyExist(){
        Warehouse warehouse = new Warehouse();
        warehouse.setName("имя");
        Mockito.when(warehouseService.addNewWarehouse(warehouse))
                .thenThrow(new WarehouseValidationException("Такой склад уже существует!"));

        ResponseEntity<ResponseDto<Warehouse>> response = warehouseController.addNewWarehouse(warehouse);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("Такой склад уже существует!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());

    }

    @Test
    public void addNewWarehouseTestFailInternalError(){
        Warehouse warehouse = new Warehouse();
        warehouse.setName("имя");
        Mockito.when(warehouseService.addNewWarehouse(warehouse)).thenThrow(new RuntimeException("Внутренняя ошибка"));

        ResponseEntity<ResponseDto<Warehouse>> response = warehouseController.addNewWarehouse(warehouse);

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, склад не добавлен!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());

    }

}

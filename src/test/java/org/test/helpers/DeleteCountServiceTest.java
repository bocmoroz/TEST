package org.test.helpers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.test.entity.Product;
import org.test.entity.Warehouse;
import org.test.repository.ProductRepository;
import org.test.repository.WarehouseRepository;

import java.util.Collections;

@SpringBootTest(classes = {DeleteCountService.class})
@RunWith(SpringRunner.class)
public class DeleteCountServiceTest {

    @Autowired
    private DeleteCountService deleteCountService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private WarehouseRepository warehouseRepository;


    @Test
    public void firstTimeDeleteProductTest() {
        Mockito.when(productRepository.findProductByArticul(Mockito.any())).thenReturn(Collections.emptyList());

        Product product = new Product();
        product.setArticul("12345");
        String deletedArticul = deleteCountService.defineDeletedProductArticul(product);

        Assert.assertEquals("12345:deleted0", deletedArticul);
    }

    @Test
    public void secondTimeDeleteProductTest() {
        Mockito.when(productRepository.findProductByArticul("12345:deleted0"))
                .thenReturn(Collections.singletonList(new Product()));
        Mockito.when(productRepository.findProductByArticul("12345:deleted1"))
                .thenReturn(Collections.emptyList());

        Product product = new Product();
        product.setArticul("12345");
        String deletedArticul = deleteCountService.defineDeletedProductArticul(product);

        Assert.assertEquals("12345:deleted1", deletedArticul);
    }

    @Test
    public void firstTimeDeleteWarehouseTest() {
        Mockito.when(warehouseRepository.findWarehouseByName(Mockito.any())).thenReturn(Collections.emptyList());

        Warehouse warehouse = new Warehouse();
        warehouse.setName("12345");
        String deletedName = deleteCountService.defineDeletedWarehouseName(warehouse);

        Assert.assertEquals("12345:deleted0", deletedName);
    }

    @Test
    public void secondTimeDeleteWarehouseTest() {
        Mockito.when(warehouseRepository.findWarehouseByName("12345:deleted0"))
                .thenReturn(Collections.singletonList(new Warehouse()));
        Mockito.when(warehouseRepository.findWarehouseByName("12345:deleted1"))
                .thenReturn(Collections.emptyList());

        Warehouse warehouse = new Warehouse();
        warehouse.setName("12345");
        String deletedName = deleteCountService.defineDeletedWarehouseName(warehouse);

        Assert.assertEquals("12345:deleted1", deletedName);
    }

}

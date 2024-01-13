package org.warehouse.app.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.warehouse.app.dao.ProductRepository;
import org.warehouse.app.dao.WarehouseRepository;
import org.warehouse.app.model.ProductEntity;
import org.warehouse.app.model.WarehouseEntity;
import org.warehouse.app.util.DeletionCountService;

import java.util.Collections;

@SpringBootTest(classes = {DeletionCountService.class})
@RunWith(SpringRunner.class)
public class DeletionCountServiceTest {

    private static final String DEFAULT_NAME = "Name12345";

    @Autowired
    private DeletionCountService deletionCountService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private WarehouseRepository warehouseRepository;


    @Test
    public void firstTimeDeleteProductTest() {
        Mockito.when(productRepository.findByArticleContainingAndDeleted(Mockito.any()))
                .thenReturn(Collections.emptyList());

        ProductEntity product = new ProductEntity();
        product.setArticle(DEFAULT_NAME);
        String deletedArticle = deletionCountService.defineDeletedProductArticle(product);

        Assert.assertEquals(DEFAULT_NAME + "_deleted_1", deletedArticle);
    }

    @Test
    public void secondTimeDeleteProductTest() {
        Mockito.when(productRepository.findByArticleContainingAndDeleted(Mockito.any()))
                .thenReturn(Collections.singletonList(new ProductEntity()));

        ProductEntity product = new ProductEntity();
        product.setArticle(DEFAULT_NAME);
        String deletedArticle = deletionCountService.defineDeletedProductArticle(product);

        Assert.assertEquals(DEFAULT_NAME + "_deleted_2", deletedArticle);
    }

    @Test
    public void firstTimeDeleteWarehouseTest() {
        Mockito.when(warehouseRepository.findByNameContainingAndDeleted(Mockito.any()))
                .thenReturn(Collections.emptyList());

        WarehouseEntity warehouse = new WarehouseEntity(DEFAULT_NAME);
        String deletedName = deletionCountService.defineDeletedWarehouseName(warehouse);

        Assert.assertEquals(DEFAULT_NAME + "_deleted_1", deletedName);
    }

    @Test
    public void secondTimeDeleteWarehouseTest() {
        Mockito.when(warehouseRepository.findByNameContainingAndDeleted(Mockito.any()))
                .thenReturn(Collections.singletonList(new WarehouseEntity()));

        WarehouseEntity warehouse = new WarehouseEntity(DEFAULT_NAME);
        String deletedName = deletionCountService.defineDeletedWarehouseName(warehouse);

        Assert.assertEquals(DEFAULT_NAME + "_deleted_2", deletedName);
    }

}

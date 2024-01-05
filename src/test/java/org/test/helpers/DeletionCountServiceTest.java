//package org.test.helpers;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.test.entity.Product;
//import org.test.entity.Warehouse;
//import org.test.repository.ProductRepository;
//import org.test.repository.WarehouseRepository;
//
//import java.util.Collections;
//
//@SpringBootTest(classes = {DeletionCountService.class})
//@RunWith(SpringRunner.class)
//public class DeletionCountServiceTest {
//
//    @Autowired
//    private DeletionCountService deletionCountService;
//
//    @MockBean
//    private ProductRepository productRepository;
//
//    @MockBean
//    private WarehouseRepository warehouseRepository;
//
//
//    @Test
//    public void firstTimeDeleteProductTest() {
//        Mockito.when(productRepository.findProductByArticle(Mockito.any())).thenReturn(Collections.emptyList());
//
//        Product product = new Product();
//        product.setArticle("12345");
//        String deletedArticle = deletionCountService.defineDeletedProductArticle(product);
//
//        Assert.assertEquals("12345:deleted0", deletedArticle);
//    }
//
//    @Test
//    public void secondTimeDeleteProductTest() {
//        Mockito.when(productRepository.findProductByArticle("12345:deleted0"))
//                .thenReturn(Collections.singletonList(new Product()));
//        Mockito.when(productRepository.findProductByArticle("12345:deleted1"))
//                .thenReturn(Collections.emptyList());
//
//        Product product = new Product();
//        product.setArticle("12345");
//        String deletedArticle = deletionCountService.defineDeletedProductArticle(product);
//
//        Assert.assertEquals("12345:deleted1", deletedArticle);
//    }
//
//    @Test
//    public void firstTimeDeleteWarehouseTest() {
//        Mockito.when(warehouseRepository.findWarehouseByName(Mockito.any())).thenReturn(Collections.emptyList());
//
//        Warehouse warehouse = new Warehouse();
//        warehouse.setName("12345");
//        String deletedName = deletionCountService.defineDeletedWarehouseName(warehouse);
//
//        Assert.assertEquals("12345:deleted0", deletedName);
//    }
//
//    @Test
//    public void secondTimeDeleteWarehouseTest() {
//        Mockito.when(warehouseRepository.findWarehouseByName("12345:deleted0"))
//                .thenReturn(Collections.singletonList(new Warehouse()));
//        Mockito.when(warehouseRepository.findWarehouseByName("12345:deleted1"))
//                .thenReturn(Collections.emptyList());
//
//        Warehouse warehouse = new Warehouse();
//        warehouse.setName("12345");
//        String deletedName = deletionCountService.defineDeletedWarehouseName(warehouse);
//
//        Assert.assertEquals("12345:deleted1", deletedName);
//    }
//
//}

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
import org.test.dto.product.ProductBuilderDto;
import org.test.dto.product.ProductDto;
import org.test.entity.Product;
import org.test.exception.ProductValidationException;
import org.test.helpers.EntityRequestValidationService;
import org.test.service.ProductService;

import java.util.Collections;
import java.util.List;

@SpringBootTest(classes = {ProductController.class})
@RunWith(SpringRunner.class)
public class ProductControllerTest {

    @Autowired
    private ProductController productController;

    @MockBean
    private ProductService productService;

    @MockBean
    private EntityRequestValidationService entityRequestValidationService;

    @Test
    public void getProductsTestSuccess() {
        Mockito.when(productService.getProducts()).thenReturn(Collections.singletonList(new Product()));

        ResponseEntity<ResponseDto<List<ProductDto>>> response = productController.getProducts();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Продукты успешно получены!", response.getBody().getMessage());
        Assert.assertEquals(1, response.getBody().getBody().size());
    }

    @Test
    public void getProductsTestFailNotFound() {
        Mockito.when(productService.getProducts()).thenThrow(new ProductValidationException("В БД нет продуктов!"));

        ResponseEntity<ResponseDto<List<ProductDto>>> response = productController.getProducts();

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("В БД нет продуктов!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getProductsTestFailInternalError() {
        Mockito.when(productService.getProducts()).thenThrow(new RuntimeException());

        ResponseEntity<ResponseDto<List<ProductDto>>> response = productController.getProducts();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, продукты не получены!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getProductByArticleTestSuccess() {
        Product product = new Product();
        product.setArticul("артикул");
        Mockito.when(productService.getProductByArticle(product.getArticul())).thenReturn(product);

        ResponseEntity<ResponseDto<ProductDto>> response = productController.getProductByArticle(product.getArticul());

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Продукт успешно получен!", response.getBody().getMessage());
        Assert.assertEquals(response.getBody().getBody(), ProductDto.create(product));
    }

    @Test
    public void getProductByArticleTestFailNotFound() {
        Product product = new Product();
        product.setArticul("артикул");
        Mockito.when(productService.getProductByArticle(product.getArticul())).thenThrow(
                new ProductValidationException("Продукт с артикулом  " + product.getArticul() + " не существует!"));

        ResponseEntity<ResponseDto<ProductDto>> response = productController.getProductByArticle(product.getArticul());

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("Продукт с артикулом  " + product.getArticul()
                + " не существует!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void getProductByArticleTestFailInternalError() {
        Mockito.when(productService.getProductByArticle(Mockito.any()))
                .thenThrow(new RuntimeException("Внутренняя ошибка"));

        ResponseEntity<ResponseDto<ProductDto>> response = productController.getProductByArticle(Mockito.any());

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, продукт не получен!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());
    }

    @Test
    public void addNewProductTestSuccess() {
        Product product = new Product();
        product.setArticul("артикул");
        product.setName("имя");
        Mockito.when(productService.addNewProduct(Mockito.any())).thenReturn(product);

        ResponseEntity<ResponseDto<ProductBuilderDto>> response = productController
                .addNewProduct(ProductBuilderDto.create(product));

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(0, response.getBody().getStatus());
        Assert.assertEquals("Продукт успешно добавлен!", response.getBody().getMessage());
        Assert.assertEquals(response.getBody().getBody(), ProductBuilderDto.create(product));
    }

    @Test
    public void addNewProductTestFailAlreadyExist() {
        Product product = new Product();
        product.setArticul("артикул");
        product.setName("имя");

        Mockito.when(productService.addNewProduct(Mockito.any()))
                .thenThrow(new ProductValidationException("Артикул " + product.getArticul() + " уже существует!"));

        ResponseEntity<ResponseDto<ProductBuilderDto>> response =
                productController.addNewProduct(new ProductBuilderDto());

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().getStatus());
        Assert.assertEquals("Артикул " + product.getArticul() + " уже существует!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());

    }

    @Test
    public void addNewProductTestFailInternalError() {
        Mockito.when(productService.addNewProduct(Mockito.any())).thenThrow(new RuntimeException("Внутренняя ошибка"));

        ResponseEntity<ResponseDto<ProductBuilderDto>> response =
                productController.addNewProduct(new ProductBuilderDto());

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, response.getBody().getStatus());
        Assert.assertEquals("Внутренняя ошибка, продукт не добавлен!", response.getBody().getMessage());
        Assert.assertNull(response.getBody().getBody());

    }

}

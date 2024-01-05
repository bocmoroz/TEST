//package org.test.controller;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.test.Application;
//import org.test.dto.ResponseDto;
//import org.test.dto.product.ProductBuilderDto;
//import org.test.dto.product.ProductDto;
//import org.test.entity.Product;
//import org.test.exception.ProductValidationException;
//import org.test.helpers.EntityRequestValidationService;
//import org.test.service.ProductService;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.test.dto.ResponseDto.StatusEnum.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
//@SpringBootTest(classes = {ProductController.class})
//@RunWith(SpringRunner.class)
//@AutoConfigureMockMvc
//public class ProductControllerTest {
//
//    @Autowired
//    private ProductController productController;
//
//    @MockBean
//    private ProductService productService;
//
//    @MockBean
//    private EntityRequestValidationService entityRequestValidationService;
//
//    private MockMvc mockMvc;
//
//    @Before
//    public void setup() {
//        this.mockMvc = MockMvcBuilders
//                .standaloneSetup(productController)     // instantiate controller.
//                .setControllerAdvice(new CommonExceptionHandlers())   // bind with controller advice.
//                .build();
//    }
//
//    @Test
//    public void getProductsTestSuccess() {
//        Mockito.when(productService.getProducts()).thenReturn(Collections.singletonList(new Product()));
//
//        ResponseDto<List<ProductDto>> response = productController.getProducts();
//
//        Assert.assertEquals(SUCCESS, response.getStatus());
//        Assert.assertNotNull(response.getBody());
//        Assert.assertEquals(1, response.getBody().size());
//        Assert.assertNull(response.getMessage());
//    }
//
//    //пример теста когда есть @controllerAdvice
//    @Test
//    public void getProductsTestFailNotFound() throws Exception {
//        String errorMessage = "There is no any product in DB";
//        Mockito.when(productService.getProducts()).thenThrow(new ProductValidationException(errorMessage));
//
//        mockMvc.perform(get("/products"))
//                .andExpect(jsonPath("$.status").value("FAIL"))
//                .andExpect(jsonPath("$.body").doesNotExist())
//                .andExpect(jsonPath("$.message").value(errorMessage));
//    }
//
//    @Test
//    public void getProductsTestFailInternalError() {
//        String errorMessage = "Internal server error";
//        Mockito.when(productService.getProducts()).thenThrow(new RuntimeException(errorMessage));
//
//        ResponseDto<List<ProductDto>> response = productController.getProducts();
//
//        Assert.assertEquals(ERROR, response.getStatus());
//        Assert.assertNull(response.getBody());
//        Assert.assertEquals(errorMessage, response.getMessage());
//    }
//
//    @Test
//    public void getProductByArticleTestSuccess() {
//        Product product = new Product();
//        product.setArticul("article1");
//        Mockito.when(productService.getProductByArticle(product.getArticul())).thenReturn(product);
//
//        ResponseDto<ProductDto> response = productController.getProductByArticle(product.getArticul());
//
//        Assert.assertEquals(SUCCESS, response.getStatus());
//        Assert.assertNotNull(response.getBody());
//        Assert.assertEquals(response.getBody(), ProductDto.create(product));
//        Assert.assertNull(response.getMessage());
//    }
//
//    @Test
//    public void getProductByArticleTestFailNotFound() {
//        Product product = new Product();
//        product.setArticul("article1");
//
//        String errorMessage = String.format("There is no product with article %s", product.getArticul());
//        Mockito.when(productService.getProductByArticle(product.getArticul())).thenThrow(
//                new ProductValidationException(errorMessage));
//
//        ResponseDto<ProductDto> response = productController.getProductByArticle(product.getArticul());
//
//        Assert.assertEquals(FAIL, response.getStatus());
//        Assert.assertNull(response.getBody());
//        Assert.assertEquals(errorMessage, response.getMessage());
//    }
//
//    @Test
//    public void getProductByArticleTestFailInternalError() {
//        String errorMessage = "Internal server error";
//        Mockito.when(productService.getProductByArticle(Mockito.any())).thenThrow(new RuntimeException(errorMessage));
//
//        ResponseDto<ProductDto> response = productController.getProductByArticle(Mockito.any());
//
//        Assert.assertEquals(ERROR, response.getStatus());
//        Assert.assertNull(response.getBody());
//        Assert.assertEquals(errorMessage, response.getMessage());
//    }
//
//    @Test
//    public void addNewProductTestSuccess() {
//        Product product = new Product();
//        product.setArticul("article1");
//        product.setName("name1");
//        Mockito.when(productService.addNewProduct(Mockito.any())).thenReturn(product);
//
//        ResponseDto<ProductBuilderDto> response = productController.addNewProduct(ProductBuilderDto.create(product));
//
//        Assert.assertEquals(SUCCESS, response.getStatus());
//        Assert.assertNotNull(response.getBody());
//        Assert.assertEquals(response.getBody(), ProductBuilderDto.create(product));
//        Assert.assertNull(response.getMessage());
//    }
//
//    @Test
//    public void addNewProductTestFailAlreadyExist() {
//        Product product = new Product();
//        product.setArticul("article1");
//        product.setName("name1");
//
//        String errorMessage = String.format("Article %s already exists", product.getArticul());
//        Mockito.when(productService.addNewProduct(Mockito.any())).thenThrow(new ProductValidationException(errorMessage));
//
//        ResponseDto<ProductBuilderDto> response = productController.addNewProduct(new ProductBuilderDto());
//
//        Assert.assertEquals(FAIL, response.getStatus());
//        Assert.assertNull(response.getBody());
//        Assert.assertEquals(errorMessage, response.getMessage());
//    }
//
//    @Test
//    public void addNewProductTestFailInternalError() {
//        String errorMessage = "Internal server error";
//        Mockito.when(productService.addNewProduct(Mockito.any())).thenThrow(new RuntimeException(errorMessage));
//
//        ResponseDto<ProductBuilderDto> response = productController.addNewProduct(new ProductBuilderDto());
//
//        Assert.assertEquals(ERROR, response.getStatus());
//        Assert.assertNull(response.getBody());
//        Assert.assertEquals(errorMessage, response.getMessage());
//    }
//
//}

package org.warehouse.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.product.ProductBuilderDto;
import org.warehouse.app.dto.product.ProductDto;
import org.warehouse.app.exception.ProductValidationException;
import org.warehouse.app.service.ProductService;
import org.warehouse.app.util.EntityRequestValidationService;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.warehouse.app.dto.ResponseDto.StatusEnum.*;

@SpringBootTest(classes = {ProductController.class})
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ProductControllerTest {

    private static final String DEFAULT_ARTICLE = "Article12345";
    private static final String DEFAULT_NAME = "Name12345";

    @Autowired
    private ProductController productController;

    @MockBean
    private ProductService productService;

    @MockBean
    private EntityRequestValidationService entityRequestValidationService;

    private MockMvc mockMvc;

    @PostConstruct
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(new CommonExceptionHandlers())
                .build();
    }

    @Test
    public void getProductsTestSuccess() {
        Mockito.when(productService.getProducts()).thenReturn(Collections.singletonList(Mockito.mock(ProductDto.class)));

        ResponseDto<List<ProductDto>> response = productController.getProducts();

        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().size());
        Assert.assertNull(response.getMessage());

        Mockito.verify(productService, Mockito.times(1)).getProducts();
    }

    @Test
    public void getProductsTestFailInternalError() throws Exception {
        String errorMessage = "Internal server error";
        Mockito.when(productService.getProducts()).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(get("/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(productService, Mockito.times(1)).getProducts();
    }

    @Test
    public void getProductByArticleTestSuccess() {
        ProductDto product = new ProductDto();
        product.setArticle(DEFAULT_ARTICLE);

        Mockito.when(productService.getProduct(Mockito.anyString())).thenReturn(product);

        ResponseDto<ProductDto> response = productController.getProductByArticle(DEFAULT_ARTICLE);

        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(response.getBody(), product);
        Assert.assertEquals(response.getBody().getArticle(), product.getArticle());
        Assert.assertNull(response.getMessage());

        Mockito.verify(productService, Mockito.times(1)).getProduct(Mockito.anyString());
    }

    @Test
    public void getProductByArticleTestFailNotFound() throws Exception {
        String errorMessage = String.format("There is no product with article %s", DEFAULT_ARTICLE);
        Mockito.when(productService.getProduct(DEFAULT_ARTICLE)).thenThrow(
                new ProductValidationException(errorMessage));

        mockMvc.perform(get("/products/{article}", DEFAULT_ARTICLE))
                .andExpect(jsonPath("$.status").value(FAIL.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(productService, Mockito.times(1)).getProduct(Mockito.anyString());
    }

    @Test
    public void getProductByArticleTestFailInternalError() throws Exception {
        String errorMessage = "Internal server error";
        Mockito.when(productService.getProduct(DEFAULT_ARTICLE)).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(get("/products/{article}", DEFAULT_ARTICLE))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(productService, Mockito.times(1)).getProduct(Mockito.anyString());
    }

    @Test
    public void addNewProductTestSuccess() {
        ProductBuilderDto productBuilderDto = new ProductBuilderDto();
        productBuilderDto.setArticle(DEFAULT_ARTICLE);
        productBuilderDto.setName(DEFAULT_NAME);

        ProductDto product = new ProductDto();
        product.setArticle(DEFAULT_ARTICLE);
        product.setName(DEFAULT_NAME);

        Mockito.when(productService.addNewProduct(DEFAULT_ARTICLE, DEFAULT_NAME)).thenReturn(product);

        ResponseDto<ProductDto> response = productController.addProduct(productBuilderDto);

        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(response.getBody(), product);
        Assert.assertEquals(response.getBody().getArticle(), product.getArticle());
        Assert.assertEquals(response.getBody().getName(), product.getName());
        Assert.assertNull(response.getMessage());

        Mockito.verify(productService, Mockito.times(1)).addNewProduct(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void addNewProductTestFailAlreadyExist() throws Exception {
        ProductBuilderDto productBuilderDto = new ProductBuilderDto();
        productBuilderDto.setArticle(DEFAULT_ARTICLE);
        productBuilderDto.setName(DEFAULT_NAME);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(productBuilderDto);

        String errorMessage = String.format("Article %s already exists", DEFAULT_ARTICLE);
        Mockito.when(productService.addNewProduct(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new ProductValidationException(errorMessage));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(jsonPath("$.status").value(FAIL.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(productService, Mockito.times(1)).addNewProduct(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void addNewProductTestFailInternalError() throws Exception {
        ProductBuilderDto productBuilderDto = new ProductBuilderDto();
        productBuilderDto.setArticle(DEFAULT_ARTICLE);
        productBuilderDto.setName(DEFAULT_NAME);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(productBuilderDto);

        String errorMessage = "Internal server error";
        Mockito.when(productService.addNewProduct(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR.toString()))
                .andExpect(jsonPath("$.body").doesNotExist())
                .andExpect(jsonPath("$.message").value(errorMessage));

        Mockito.verify(productService, Mockito.times(1)).addNewProduct(Mockito.anyString(), Mockito.anyString());
    }
}

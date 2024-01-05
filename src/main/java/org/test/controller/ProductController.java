package org.test.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.dto.product.ProductBuilderDto;
import org.test.dto.product.ProductDto;
import org.test.exception.ProductValidationException;
import org.test.helpers.EntityRequestValidationService;
import org.test.service.ProductService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/products")
@Api
public class ProductController {

    private final ProductService productService;
    private final EntityRequestValidationService requestValidationService;

    @Autowired
    public ProductController(ProductService productService, EntityRequestValidationService requestValidationService) {
        this.productService = productService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    public ResponseDto<List<ProductDto>> getProducts() {
        List<ProductDto> listProducts = productService.getProducts();
        log.info("All products: {}", listProducts);
        return ResponseDto.success(listProducts);
    }

    @GetMapping(path = "/{article}")
    public ResponseDto<ProductDto> getProductByArticle(@PathVariable String article) {
        ProductDto productDto = productService.getProduct(article);
        return ResponseDto.success(productDto);
    }

    @PostMapping
    public ResponseDto<ProductDto> addNewProduct(@RequestBody ProductBuilderDto productBuilderDto) {
        String article = productBuilderDto.getArticle();
        String name = productBuilderDto.getName();
        requestValidationService.validateProductAddRequest(article, name);
        ProductDto addedProduct = productService.addNewProduct(article, name);
        log.info("Product was added : {}", addedProduct);
        return ResponseDto.success(addedProduct);
    }

    @PutMapping(path = "/{article}")
    public ResponseDto<ProductDto> updateProduct(@PathVariable String article,
                                                 @RequestParam String newProductName) {
        requestValidationService.validateProductUpdateRequest(article, newProductName);
        ProductDto updatedProduct = productService.updateProduct(article, newProductName);
        log.info("Product name with article {} updated to {}", article, newProductName);
        return ResponseDto.success(updatedProduct);
    }

    @DeleteMapping(path = "/{article}")
    public ResponseDto<ProductDto> deleteProduct(@PathVariable String article) {
        ProductDto deletedProduct = productService.deleteProduct(article);
        log.info("Product with article {} deleted", article);
        return ResponseDto.success(deletedProduct);
    }

    @ExceptionHandler(ProductValidationException.class)
    public ResponseDto<Object> handleProductValidationException(ProductValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

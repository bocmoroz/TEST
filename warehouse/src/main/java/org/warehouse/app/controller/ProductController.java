package org.warehouse.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.warehouse.app.dto.ResponseDto;
import org.warehouse.app.dto.product.ProductBuilderDto;
import org.warehouse.app.dto.product.ProductDto;
import org.warehouse.app.exception.ProductValidationException;
import org.warehouse.app.service.ProductService;
import org.warehouse.app.util.EntityRequestValidationService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/products")
@Api("Resource for products")
public class ProductController {

    private final ProductService productService;
    private final EntityRequestValidationService requestValidationService;

    @Autowired
    public ProductController(ProductService productService, EntityRequestValidationService requestValidationService) {
        this.productService = productService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    @ApiOperation("Getting all products")
    public ResponseDto<List<ProductDto>> getProducts() {
        List<ProductDto> listProducts = productService.getProducts();
        return ResponseDto.success(listProducts);
    }

    @GetMapping(path = "/{article}")
    @ApiOperation("Getting product by article")
    public ResponseDto<ProductDto> getProductByArticle(@PathVariable String article) {
        ProductDto productDto = productService.getProduct(article);
        return ResponseDto.success(productDto);
    }

    @PostMapping
    @ApiOperation("Adding new product")
    public ResponseDto<ProductDto> addProduct(@RequestBody ProductBuilderDto productBuilderDto) {
        String article = productBuilderDto.getArticle();
        String name = productBuilderDto.getName();
        requestValidationService.validateProductAddRequest(article, name);
        ProductDto addedProduct = productService.addNewProduct(article, name);
        return ResponseDto.success(addedProduct);
    }

    @PutMapping(path = "/{article}")
    @ApiOperation("Updating product name by article")
    public ResponseDto<ProductDto> updateProduct(@PathVariable String article,
                                                 @RequestParam String newProductName) {
        requestValidationService.validateProductUpdateRequest(article, newProductName);
        ProductDto updatedProduct = productService.updateProduct(article, newProductName);
        return ResponseDto.success(updatedProduct);
    }

    @DeleteMapping(path = "/{article}")
    @ApiOperation("Deletion product")
    public ResponseDto<ProductDto> deleteProduct(@PathVariable String article) {
        ProductDto deletedProduct = productService.deleteProduct(article);
        return ResponseDto.success(deletedProduct);
    }

    @ExceptionHandler(ProductValidationException.class)
    public ResponseDto<Object> handleProductValidationException(ProductValidationException exception) {
        log.error("exception", exception);
        return ResponseDto.fail(exception.getMessage());
    }

}

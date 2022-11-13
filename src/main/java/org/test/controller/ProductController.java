package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.dto.product.ProductBuilderDto;
import org.test.dto.product.ProductDto;
import org.test.entity.Product;
import org.test.exception.ProductValidationException;
import org.test.helpers.EntityRequestValidationService;
import org.test.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(path = "/products")
public class ProductController {

    private final ProductService productService;
    private final EntityRequestValidationService requestValidationService;

    @Autowired
    public ProductController(ProductService productService, EntityRequestValidationService requestValidationService) {
        this.productService = productService;
        this.requestValidationService = requestValidationService;
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<ProductDto>>> getProducts() {

        ResponseDto<List<ProductDto>> responseDto;

        try {
            List<Product> listProducts = productService.getProducts();
            List<ProductDto> listProductsDto = listProducts.stream()
                    .map(ProductDto::create)
                    .collect(Collectors.toList());
            log.info("products {}", listProductsDto);
            responseDto = new ResponseDto<>(0, "Продукты успешно получены!", listProductsDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (ProductValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, продукты не получены!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/product")
    public ResponseEntity<ResponseDto<ProductDto>> getProductByArticle(
            @RequestParam String articul) {

        ResponseDto<ProductDto> responseDto;

        try {
            Product product = productService.getProductByArticle(articul);
            ProductDto productDto = ProductDto.create(product);
            log.info("product {}", productDto);
            responseDto = new ResponseDto<>(0, "Продукт успешно получен!", productDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (ProductValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, продукт не получен!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/add")
    public ResponseEntity<ResponseDto<ProductBuilderDto>> addNewProduct(@RequestBody ProductBuilderDto productBuilderDto) {

        ResponseDto<ProductBuilderDto> responseDto;

        try {
            log.info("product {}", productBuilderDto);
            requestValidationService.validateProductAddRequest(productBuilderDto.getArticul(), productBuilderDto.getName());
            Product product = new Product(productBuilderDto.getArticul(), productBuilderDto.getName());
            Product addedProduct = productService.addNewProduct(product);
            ProductBuilderDto addedProductBuilderDto = ProductBuilderDto.create(addedProduct);
            log.info("addedProduct {}", addedProductBuilderDto);
            responseDto = new ResponseDto<>(0, "Продукт успешно добавлен!", addedProductBuilderDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (ProductValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, продукт не добавлен!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping(path = "/update/{productArticul}")
    public ResponseEntity<ResponseDto<ProductDto>> updateProduct(
            @PathVariable("productArticul") String productArticul,
            @RequestParam(required = false) String newProductName) {

        ResponseDto<ProductDto> responseDto;

        try {
            requestValidationService.validateProductUpdateRequest(productArticul, newProductName);
            Product updatedProduct = productService.updateProduct(productArticul, newProductName);
            ProductDto updatedProductDto = ProductDto.create(updatedProduct);
            log.info("product {}", updatedProductDto);
            responseDto = new ResponseDto<>(0, "Имя продукта успешно обновлено!", updatedProductDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (ProductValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, имя продукта не обновлено!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<ResponseDto<ProductDto>> deleteProduct(
            @RequestParam(name = "deleteProductArticul") String articul) {

        ResponseDto<ProductDto> responseDto;

        try {
            Product deletedProduct = productService.deleteProduct(articul);
            ProductDto deletedProductDto = ProductDto.create(deletedProduct);
            log.info("deletedProduct {}", deletedProductDto);
            responseDto = new ResponseDto<>(0, "Продукт успешно удалён!", deletedProductDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (ProductValidationException e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(1, e.getMessage(), null);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("exception", e);
            responseDto = new ResponseDto<>(2, "Внутренняя ошибка, продукт не удалён!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

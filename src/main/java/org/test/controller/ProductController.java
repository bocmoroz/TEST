package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.dto.ResponseDto;
import org.test.entity.Product;
import org.test.exception.ProductValidationException;
import org.test.helpers.EntityRequestValidationService;
import org.test.service.ProductService;

import java.util.List;

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
    public ResponseEntity<ResponseDto<List<Product>>> getProducts() {

        ResponseDto<List<Product>> responseDto;

        try {
            List<Product> listProducts = productService.getProducts();
            log.info("products {}", listProducts);
            responseDto = new ResponseDto<>(0, "Продукты успешно получены!", listProducts);
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
    public ResponseEntity<ResponseDto<Product>> getProductByArticle(
            @RequestParam String articul) {

        ResponseDto<Product> responseDto;

        try {
            Product product = productService.getProductByArticle(articul);
            log.info("product {}", product);
            responseDto = new ResponseDto<>(0, "Продукт успешно получен!", product);
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
    public ResponseEntity<ResponseDto<Product>> addNewProduct(@RequestBody Product product) {

        ResponseDto<Product> responseDto;

        try {
            log.info("product {}", product);
            requestValidationService.validateProductAddRequest(product.getArticul(), product.getName());
            Product addedProduct = productService.addNewProduct(product);
            log.info("addedProduct {}", addedProduct);
            responseDto = new ResponseDto<>(0, "Продукт успешно добавлен!", addedProduct);
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
    public ResponseEntity<ResponseDto<Product>> updateProduct(
            @PathVariable("productArticul") String productArticul,
            @RequestParam(required = false) String newProductName) {

        ResponseDto<Product> responseDto;

        try {
            requestValidationService.validateProductUpdateRequest(productArticul, newProductName);
            Product product = productService.updateProduct(productArticul, newProductName);
            log.info("product {}", product);
            responseDto = new ResponseDto<>(0, "Имя продукта успешно обновлено!", product);
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
    public ResponseEntity<ResponseDto<Product>> deleteProduct(
            @RequestParam(name = "deleteProductArticul") String articul) {

        ResponseDto<Product> responseDto;

        try {
            Product deletedProduct = productService.deleteProduct(articul);
            log.info("deletedProduct {}", deletedProduct);
            responseDto = new ResponseDto<>(0, "Продукт успешно удалён!", deletedProduct);
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

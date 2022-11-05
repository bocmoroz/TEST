package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.dto.LeftoverReportDto;
import org.test.dto.ProductReportDto;
import org.test.dto.ResponseDto;
import org.test.entity.Product;
import org.test.entity.Warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(path = "/report")
public class ReportController {

    private final ProductController productController;
    private final WarehouseController warehouseController;

    @Autowired
    public ReportController(ProductController productController, WarehouseController warehouseController) {
        this.productController = productController;
        this.warehouseController = warehouseController;
    }

    @GetMapping("/products/json")
    public ResponseEntity<ResponseDto<List<ProductReportDto>>> getProductsReport() {

        ResponseDto<List<ProductReportDto>> responseDto;

        List<Product> productList = productController.getProducts().getBody().getBody();

        if (productList == null) {
            responseDto = new ResponseDto<>(2, "Ошибка при получении продуктов!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<ProductReportDto> productDtoList = productList.stream()
                .map(product -> {
                    ProductReportDto dto = new ProductReportDto();
                    dto.setArticul(product.getArticul());
                    dto.setName(product.getName());
                    dto.setLastIncomePrice(product.getLastIncomePrice());
                    dto.setLastSalePrice(product.getLastSalePrice());
                    return dto;
                }).collect(Collectors.toList());

        responseDto = new ResponseDto<>(0, "Отчёт успешно получен!", productDtoList);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=productsAll.json")
                .body(responseDto);
    }

    @GetMapping("/products/{productArticul}/json")
    public ResponseEntity<ResponseDto<ProductReportDto>> getProductByArticulReport(
            @PathVariable("productArticul") String productArticul) {

        ResponseDto<ProductReportDto> responseDto;

        Product product = productController.getProductByArticle(productArticul).getBody().getBody();

        if (product == null) {
            responseDto = new ResponseDto<>(2, "Ошибка при получении продукта!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ProductReportDto productDto = new ProductReportDto();
        productDto.setArticul(product.getArticul());
        productDto.setName(product.getName());
        productDto.setLastIncomePrice(product.getLastIncomePrice());
        productDto.setLastSalePrice(product.getLastSalePrice());

        responseDto = new ResponseDto<>(0, "Отчёт успешно получен!", productDto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=product"
                        + productArticul
                        + ".json")
                .body(responseDto);

    }

    @GetMapping("/leftovers/json")
    public ResponseEntity<ResponseDto<List<LeftoverReportDto>>> getLeftoversReport() {

        ResponseDto<List<LeftoverReportDto>> responseDto;

        List<Warehouse> warehouseList = warehouseController.getWarehouses().getBody().getBody();

        if (warehouseList == null) {
            responseDto = new ResponseDto<>(2, "Ошибка при получении складов!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<LeftoverReportDto> leftoverReportDtoList = new ArrayList<>();

        warehouseList.forEach(wh -> wh.getProducts().forEach(productCount -> {

            Optional<LeftoverReportDto> optionalLeftoverReportDto = leftoverReportDtoList.stream()
                    .filter(p -> p.getArticul().equals(productCount.getProduct().getArticul())).findAny();

            if (optionalLeftoverReportDto.isPresent()) {
                LeftoverReportDto leftoverReportDto = optionalLeftoverReportDto.get();
                leftoverReportDto.setCount(leftoverReportDto.getCount() + productCount.getCount());
            } else {
                LeftoverReportDto dto = new LeftoverReportDto();
                dto.setArticul(productCount.getProduct().getArticul());
                dto.setName(productCount.getProduct().getName());
                dto.setCount(productCount.getCount());
                leftoverReportDtoList.add(dto);
            }
        }));

        responseDto = new ResponseDto<>(0, "Отчёт успешно получен!", leftoverReportDtoList);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=leftoversAll.json")
                .body(responseDto);
    }

    @GetMapping("/leftovers/{warehouseName}/json")
    public ResponseEntity<ResponseDto<List<LeftoverReportDto>>> getLeftoversByWarehouseNameReport(
            @PathVariable("warehouseName") String warehouseName) {

        ResponseDto<List<LeftoverReportDto>> responseDto;

        Warehouse warehouse = warehouseController.getWarehouseByName(warehouseName).getBody().getBody();

        if (warehouse == null) {
            responseDto = new ResponseDto<>(2, "Ошибка при получении склада!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<LeftoverReportDto> leftoverReportDtoList = warehouse.getProducts().stream()
                .map(productCount -> {
                    LeftoverReportDto dto = new LeftoverReportDto();
                    dto.setArticul(productCount.getProduct().getArticul());
                    dto.setName(productCount.getProduct().getName());
                    dto.setCount(productCount.getCount());
                    return dto;
                }).collect(Collectors.toList());

        responseDto = new ResponseDto<>(0, "Отчёт успешно получен!", leftoverReportDtoList);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=leftoversOn"
                        + warehouseName
                        + ".json")
                .body(responseDto);
    }
}

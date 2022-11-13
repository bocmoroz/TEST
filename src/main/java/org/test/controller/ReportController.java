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
import org.test.dto.ResponseDto;
import org.test.dto.leftover.LeftoverReportDto;
import org.test.dto.product.ProductDto;
import org.test.dto.warehouse.WarehouseDto;

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
    public ResponseEntity<ResponseDto<List<ProductDto>>> getProductsReport() {

        ResponseDto<List<ProductDto>> responseDto;

        List<ProductDto> productDtoList = productController.getProducts().getBody().getBody();

        if (productDtoList == null) {
            responseDto = new ResponseDto<>(2, "Ошибка при получении продуктов!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseDto = new ResponseDto<>(0, "Отчёт успешно получен!", productDtoList);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=productsAll.json")
                .body(responseDto);
    }

    @GetMapping("/products/{productArticul}/json")
    public ResponseEntity<ResponseDto<ProductDto>> getProductByArticulReport(
            @PathVariable("productArticul") String productArticul) {

        ResponseDto<ProductDto> responseDto;

        ProductDto productDto = productController.getProductByArticle(productArticul).getBody().getBody();

        if (productDto == null) {
            responseDto = new ResponseDto<>(2, "Ошибка при получении продукта!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

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

        List<WarehouseDto> warehouseList = warehouseController.getWarehouses().getBody().getBody();

        if (warehouseList == null) {
            responseDto = new ResponseDto<>(2, "Ошибка при получении складов!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<LeftoverReportDto> leftoverReportDtoList = new ArrayList<>();

        warehouseList.forEach(wh -> wh.getProducts().forEach(warehouseProductDto -> {

            Optional<LeftoverReportDto> optionalLeftoverReportDto = leftoverReportDtoList.stream()
                    .filter(p -> p.getArticul().equals(warehouseProductDto.getArticul())).findAny();

            if (optionalLeftoverReportDto.isPresent()) {
                LeftoverReportDto leftoverReportDto = optionalLeftoverReportDto.get();
                leftoverReportDto.setCount(leftoverReportDto.getCount() + warehouseProductDto.getCount());
            } else {
                LeftoverReportDto dto = new LeftoverReportDto();
                dto.setArticul(warehouseProductDto.getArticul());
                dto.setName(warehouseProductDto.getName());
                dto.setCount(warehouseProductDto.getCount());
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

        WarehouseDto warehouseDto = warehouseController.getWarehouseByName(warehouseName).getBody().getBody();

        if (warehouseDto == null) {
            responseDto = new ResponseDto<>(2, "Ошибка при получении склада!", null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<LeftoverReportDto> leftoverReportDtoList = warehouseDto.getProducts().stream()
                .map(warehouseProductDto -> {
                    LeftoverReportDto dto = new LeftoverReportDto();
                    dto.setArticul(warehouseProductDto.getArticul());
                    dto.setName(warehouseProductDto.getName());
                    dto.setCount(warehouseProductDto.getCount());
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

package org.test.dto;

import lombok.Data;
import org.test.entity.WarehouseSale;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class WarehouseSaleDto {

    private String warehouseName;
    private List<ProductSaleDto> products;

    public static WarehouseSaleDto create(WarehouseSale warehouseSale) {
        WarehouseSaleDto warehouseSaleDto = new WarehouseSaleDto();

        warehouseSaleDto.setWarehouseName(warehouseSale.getWarehouse().getName());
        warehouseSaleDto.setProducts(warehouseSale.getProducts().stream().map(
                productCount -> {
                    ProductSaleDto productDto = new ProductSaleDto();
                    productDto.setArticul(productCount.getProduct().getArticul());
                    productDto.setName(productCount.getProduct().getName());
                    productDto.setPrice(productCount.getProduct().getLastSalePrice());
                    productDto.setCount(productCount.getCount());
                    return productDto;
                }).collect(Collectors.toList()));

        return warehouseSaleDto;
    }
}

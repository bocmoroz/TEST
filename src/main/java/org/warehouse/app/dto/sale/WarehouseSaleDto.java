package org.warehouse.app.dto.sale;

import lombok.Data;
import org.warehouse.app.entity.WarehouseSale;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class WarehouseSaleDto {

    private Long id;
    private String warehouseName;
    private List<ProductSaleDto> products;

    public static WarehouseSaleDto create(WarehouseSale warehouseSale) {
        WarehouseSaleDto warehouseSaleDto = new WarehouseSaleDto();

        warehouseSaleDto.setId(warehouseSale.getId());
        warehouseSaleDto.setWarehouseName(warehouseSale.getWarehouse().getName());
        warehouseSaleDto.setProducts(warehouseSale.getProducts().stream().map(
                productCount -> {
                    ProductSaleDto productDto = new ProductSaleDto();
                    productDto.setArticle(productCount.getProduct().getArticle());
                    productDto.setName(productCount.getProduct().getName());
                    productDto.setPrice(productCount.getPrice());
                    productDto.setCount(productCount.getCount());
                    return productDto;
                }).collect(Collectors.toList()));

        return warehouseSaleDto;
    }
}

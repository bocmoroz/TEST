package org.warehouse.app.dto.transportation;

import lombok.Data;
import org.warehouse.app.dto.ProductTransportationDto;
import org.warehouse.app.entity.WarehouseTransportation;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class WarehouseTransportationDto {

    private Long id;
    private String warehouseNameFrom;
    private String warehouseNameTo;

    private List<ProductTransportationDto> products;

    public static WarehouseTransportationDto create(WarehouseTransportation warehouseTransportation) {

        WarehouseTransportationDto warehouseTransportationDto = new WarehouseTransportationDto();

        warehouseTransportationDto.setId(warehouseTransportation.getId());
        warehouseTransportationDto.setWarehouseNameFrom(warehouseTransportation.getWarehouseFrom().getName());
        warehouseTransportationDto.setWarehouseNameTo(warehouseTransportation.getWarehouseTo().getName());
        warehouseTransportationDto.setProducts(warehouseTransportation.getProducts().stream().map(
                productCount -> {
                    ProductTransportationDto productDto = new ProductTransportationDto();
                    productDto.setArticle(productCount.getProduct().getArticle());
                    productDto.setName(productCount.getProduct().getName());
                    productDto.setCount(productCount.getCount());
                    return productDto;
                }).collect(Collectors.toList()));

        return warehouseTransportationDto;
    }
}

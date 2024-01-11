package org.warehouse.app.dto.product;

import lombok.Data;
import org.warehouse.app.model.ProductEntity;

import java.math.BigDecimal;

@Data
public class ProductDto {

    private String article;
    private String name;
    private BigDecimal lastIncomePrice;
    private BigDecimal lastSalePrice;

    public static ProductDto create(ProductEntity productEntity) {
        ProductDto productDto = new ProductDto();
        productDto.setArticle(productEntity.getArticle());
        productDto.setName(productEntity.getName());
        productDto.setLastIncomePrice(productEntity.getLastIncomePrice());
        productDto.setLastSalePrice(productEntity.getLastSalePrice());
        return productDto;
    }

}

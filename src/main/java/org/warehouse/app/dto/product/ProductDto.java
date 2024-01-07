package org.warehouse.app.dto.product;

import lombok.Data;
import org.warehouse.app.entity.Product;

@Data
public class ProductDto {

    private String article;
    private String name;
    private Long lastIncomePrice;
    private Long lastSalePrice;

    public static ProductDto create(Product product) {
        ProductDto productDto = new ProductDto();

        productDto.setArticle(product.getArticle());
        productDto.setName(product.getName());
        productDto.setLastIncomePrice(product.getLastIncomePrice());
        productDto.setLastSalePrice(product.getLastSalePrice());

        return productDto;
    }

}

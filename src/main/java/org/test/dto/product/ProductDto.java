package org.test.dto.product;

import lombok.Data;
import org.test.entity.Product;

@Data
public class ProductDto {

    private String articul;
    private String name;
    private Long lastIncomePrice;
    private Long lastSalePrice;

    public static ProductDto create(Product product) {
        ProductDto productDto = new ProductDto();

        productDto.setArticul(product.getArticul());
        productDto.setName(product.getName());
        productDto.setLastIncomePrice(productDto.getLastIncomePrice());
        productDto.setLastSalePrice(productDto.getLastSalePrice());

        return productDto;
    }

}

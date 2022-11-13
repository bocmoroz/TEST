package org.test.dto.product;

import lombok.Data;
import org.test.entity.Product;

@Data
public class ProductBuilderDto {

    private String articul;
    private String name;

    public static ProductBuilderDto create(Product product) {
        ProductBuilderDto productBuilderDto = new ProductBuilderDto();

        productBuilderDto.setArticul(product.getArticul());
        productBuilderDto.setName(product.getName());

        return productBuilderDto;
    }
}

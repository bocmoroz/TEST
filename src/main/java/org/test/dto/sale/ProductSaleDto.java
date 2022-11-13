package org.test.dto.sale;

import lombok.Data;

@Data
public class ProductSaleDto {

    private String articul;
    private String name;
    private Long price;
    private Integer count;

}

package org.test.dto.income;

import lombok.Data;

@Data
public class ProductIncomeDto {

    private String article;
    private String name;
    private Long price;
    private Integer count;

}

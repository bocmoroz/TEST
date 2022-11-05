package org.test.dto;

import lombok.Data;

@Data
public class ProductReportDto {

    private String articul;
    private String name;
    private Long lastIncomePrice;
    private Long lastSalePrice;

}

package org.warehouse.app.dto.leftover;

import lombok.Data;

@Data
public class StockReportDto {

    private String article;
    private String name;
    private Integer count;
}

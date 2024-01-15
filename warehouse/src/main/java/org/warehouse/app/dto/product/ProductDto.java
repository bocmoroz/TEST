package org.warehouse.app.dto.product;

import org.warehouse.app.model.ProductEntity;

import java.math.BigDecimal;

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

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLastIncomePrice() {
        return lastIncomePrice;
    }

    public void setLastIncomePrice(BigDecimal lastIncomePrice) {
        this.lastIncomePrice = lastIncomePrice;
    }

    public BigDecimal getLastSalePrice() {
        return lastSalePrice;
    }

    public void setLastSalePrice(BigDecimal lastSalePrice) {
        this.lastSalePrice = lastSalePrice;
    }
}

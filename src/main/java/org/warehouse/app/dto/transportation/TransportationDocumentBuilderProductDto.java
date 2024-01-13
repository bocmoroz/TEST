package org.warehouse.app.dto.transportation;

import org.codehaus.plexus.util.StringUtils;

import java.math.BigDecimal;

public class TransportationDocumentBuilderProductDto {

    private String article;
    private String name;
    private Integer count;
    private BigDecimal price;

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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean containsInvalidCommonFields() {
        return StringUtils.isEmpty(article) || StringUtils.isEmpty(name) || count == null || count < 0;
    }

    public boolean containsInvalidPriceField() {
        return price == null || price.compareTo(BigDecimal.ZERO) < 0;
    }

    @Override
    public String toString() {
        return "TransportationDocumentProduct{" +
                "article='" + article + '\'' +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", price=" + price +
                '}';
    }
}

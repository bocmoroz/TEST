package org.warehouse.app.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

@Data
@Embeddable
public class ProductCount {

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer count;

    private BigDecimal price;
}

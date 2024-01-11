package org.warehouse.app.model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

@Data
@Embeddable
public class WarehouseProduct {

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    private Integer count;
}

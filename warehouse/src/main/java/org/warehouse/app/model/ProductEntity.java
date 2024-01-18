package org.warehouse.app.model;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String article;
    private String name;

    private BigDecimal lastIncomePrice;
    private BigDecimal lastSalePrice;

    @UpdateTimestamp
    private LocalDateTime lastChangeDate;

    @Column(nullable = false)
    private Boolean deleted;

    public ProductEntity() {
        this.deleted = false;
    }

    public ProductEntity(String article, String name) {
        this.article = article;
        this.name = name;
        this.deleted = false;
    }
}

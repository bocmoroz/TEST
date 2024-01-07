package org.warehouse.app.entity;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String article;
    private String name;

    private Long lastIncomePrice;
    private Long lastSalePrice;

    @UpdateTimestamp
    private Date lastChangeDate;

    @Column(nullable = false)
    private Boolean deleted;

    public Product() {
        this.deleted = false;
    }

    public Product(String article, String name) {
        this.article = article;
        this.name = name;
        this.deleted = false;
    }

}

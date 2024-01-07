package org.warehouse.app.entity;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table
@Data
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @UpdateTimestamp
    private Date lastChangeDate;

    @Column(nullable = false)
    private Boolean deleted;

    @ElementCollection
    @CollectionTable(name = "warehouse_products_count",
            joinColumns = @JoinColumn(name = "warehouse_id", nullable = false))
    private List<ProductCount> products = new ArrayList<>();

    public Warehouse() {
        this.deleted = false;
    }

    public Warehouse(String name) {
        this.name = name;
        this.deleted = false;
    }

}

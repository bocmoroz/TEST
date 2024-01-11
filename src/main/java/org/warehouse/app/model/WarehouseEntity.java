package org.warehouse.app.model;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Data
public class WarehouseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @UpdateTimestamp
    private LocalDateTime lastChangeDate;

    @Column(nullable = false)
    private Boolean deleted;

    @ElementCollection
    @CollectionTable(name = "warehouse_products_count",
            joinColumns = @JoinColumn(name = "warehouse_id", nullable = false))
    private List<WarehouseProduct> products = new ArrayList<>();

    public WarehouseEntity() {
        this.deleted = false;
    }

    public WarehouseEntity(String name) {
        this.name = name;
        this.deleted = false;
    }

}

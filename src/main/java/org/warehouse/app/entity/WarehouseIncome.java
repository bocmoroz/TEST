package org.warehouse.app.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table
@Data
@EntityListeners(AuditingEntityListener.class)
public class WarehouseIncome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private Date createDate;

    @OneToOne
    private Warehouse warehouse;

    @ElementCollection
    @CollectionTable(name = "income_products_count",
            joinColumns = @JoinColumn(name = "warehouse_income_id", nullable = false))
    private List<ProductCount> products;

}

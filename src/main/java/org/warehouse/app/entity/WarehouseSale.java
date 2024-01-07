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
public class WarehouseSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private Date createDate;

    @OneToOne
    private Warehouse warehouse;

    @ElementCollection
    @CollectionTable(name = "sale_products_count",
            joinColumns = @JoinColumn(name = "warehouse_sale_id", nullable = false))
    @AttributeOverrides({
            @AttributeOverride(name = "product",
                    column = @Column(name = "product_id")),
            @AttributeOverride(name = "count",
                    column = @Column(name = "count"))
    })
    private List<ProductCount> products;

}

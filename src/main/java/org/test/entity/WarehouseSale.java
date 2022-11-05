package org.test.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table
@Data
public class WarehouseSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private Date createDate;

    @OneToOne
    private Warehouse warehouse;

    @ElementCollection
    @CollectionTable(name = "Sale_products_count",
            joinColumns = @JoinColumn(name = "WarehouseSale_id", nullable = false))
    @AttributeOverrides({
            @AttributeOverride(name = "product",
                    column = @Column(name = "product_id")),
            @AttributeOverride(name = "count",
                    column = @Column(name = "count"))
    })
    private List<ProductCount> products;

}

package org.warehouse.app.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.warehouse.app.enums.TransportationDocumentTypeEnum;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transportation_documents")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TransportationDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @CreatedDate
    private LocalDateTime createdDate;

    @OneToOne
    @JoinColumn(name = "warehouse_id_from")
    private WarehouseEntity warehouseFrom;

    @OneToOne
    @JoinColumn(name = "warehouse_id_to")
    private WarehouseEntity warehouseTo;

    @Enumerated(EnumType.STRING)
    private TransportationDocumentTypeEnum type;

    @ElementCollection
    @CollectionTable(name = "transportation_document_products",
            joinColumns = @JoinColumn(name = "transportation_document_id", nullable = false))
    private List<TransportationDocumentProduct> products;
}

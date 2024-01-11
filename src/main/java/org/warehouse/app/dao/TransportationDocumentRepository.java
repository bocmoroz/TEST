package org.warehouse.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warehouse.app.model.TransportationDocumentEntity;
import org.warehouse.app.enums.TransportationDocumentTypeEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportationDocumentRepository extends JpaRepository<TransportationDocumentEntity, Long> {

    List<TransportationDocumentEntity> findTransportationDocumentsByType(TransportationDocumentTypeEnum type);

    Optional<TransportationDocumentEntity> findTransportationDocumentById(Long id);

    Optional<TransportationDocumentEntity> findTransportationDocumentByName(String name);
}

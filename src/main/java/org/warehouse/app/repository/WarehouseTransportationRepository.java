package org.warehouse.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warehouse.app.entity.WarehouseTransportation;

import java.util.Optional;

@Repository
public interface WarehouseTransportationRepository extends JpaRepository<WarehouseTransportation, Long> {

    Optional<WarehouseTransportation> findProductTransportationById(Long id);
}

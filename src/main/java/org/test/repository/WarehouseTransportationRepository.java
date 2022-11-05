package org.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.entity.WarehouseTransportation;

import java.util.List;

@Repository
public interface WarehouseTransportationRepository extends JpaRepository<WarehouseTransportation, Long> {

    List<WarehouseTransportation> findProductTransportationById(Long id);
}

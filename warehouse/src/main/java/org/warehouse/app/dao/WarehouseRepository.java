package org.warehouse.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.warehouse.app.model.WarehouseEntity;

import java.util.List;
import java.util.Optional;


@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, Long> {

    Optional<WarehouseEntity> findWarehouseByName(String name);

    @Query(value = "SELECT * FROM Warehouse WHERE name LIKE :partialName% AND deleted = true", nativeQuery = true)
    List<WarehouseEntity> findByNameContainingAndDeleted(String partialName);

    List<WarehouseEntity> findAllByDeleted(Boolean isDeleted);
}

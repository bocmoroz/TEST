package org.warehouse.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.warehouse.app.entity.Warehouse;

import java.util.List;
import java.util.Optional;


@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findWarehouseByName(String name);

    @Query(value = "SELECT * FROM Warehouse WHERE name LIKE :partialName% AND deleted = true", nativeQuery = true)
    List<Warehouse> findByNameContainingAndDeleted(String partialName);

    List<Warehouse> findAllByDeleted(Boolean isDeleted);
}

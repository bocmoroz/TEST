package org.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.entity.Warehouse;

import java.util.List;


@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    List<Warehouse> findWarehouseByName(String name);

    List<Warehouse> findAllByDeleted(Boolean isDeleted);
}

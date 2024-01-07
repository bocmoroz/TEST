package org.warehouse.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warehouse.app.entity.WarehouseIncome;

import java.util.Optional;

@Repository
public interface WarehouseIncomeRepository extends JpaRepository<WarehouseIncome, Long> {

    Optional<WarehouseIncome> findWarehouseIncomeById(Long id);
}

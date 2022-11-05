package org.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.entity.WarehouseIncome;

import java.util.List;

@Repository
public interface WarehouseIncomeRepository extends JpaRepository<WarehouseIncome, Long> {

    List<WarehouseIncome> findWarehouseIncomeById(Long id);
}

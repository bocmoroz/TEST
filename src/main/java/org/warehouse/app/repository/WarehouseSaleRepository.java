package org.warehouse.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warehouse.app.entity.WarehouseSale;

import java.util.Optional;


@Repository
public interface WarehouseSaleRepository extends JpaRepository<WarehouseSale, Long> {

    Optional<WarehouseSale> findProductSaleById(Long id);
}
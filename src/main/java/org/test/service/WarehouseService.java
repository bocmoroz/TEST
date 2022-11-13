package org.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.entity.Warehouse;
import org.test.exception.WarehouseValidationException;
import org.test.helpers.DeleteCountService;
import org.test.repository.WarehouseRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final DeleteCountService deleteCountService;

    @Autowired
    public WarehouseService(WarehouseRepository warehouseRepository, DeleteCountService deleteCountService) {
        this.warehouseRepository = warehouseRepository;
        this.deleteCountService = deleteCountService;
    }

    public List<Warehouse> getWarehouses() {
        List<Warehouse> list = warehouseRepository.findAllByDeleted(false);
        if (list.isEmpty()) {
            throw new WarehouseValidationException("В БД нет складов!");
        }
        return list;
    }

    public Warehouse getWarehouseByName(String name) {
        List<Warehouse> warehouses = warehouseRepository.findWarehouseByName(name);
        if (warehouses.isEmpty()) {
            throw new WarehouseValidationException("Склад с именем  " + name + " не существует!");
        }

        if (warehouses.size() > 1) {
            throw new WarehouseValidationException("БД содержит более одного склада с именем " + name + "!");
        }

        return warehouses.get(0);
    }

    public Warehouse addNewWarehouse(Warehouse warehouse) {
        List<Warehouse> warehouses = warehouseRepository.findWarehouseByName(warehouse.getName());
        if (!warehouses.isEmpty()) {
            throw new WarehouseValidationException("Склад с именем " + warehouse.getName() + " уже существует!");
        }

        warehouse.setDeleted(false);

        warehouseRepository.save(warehouse);

        return warehouse;
    }

    @Transactional
    public Warehouse updateWarehouse(String oldName, String newName) {
        List<Warehouse> warehouses = warehouseRepository.findWarehouseByName(oldName);
        if (warehouses.isEmpty()) {
            throw new WarehouseValidationException("Склад с именем  " + oldName + " не существует!");
        }

        if (warehouses.size() > 1) {
            throw new WarehouseValidationException("БД содержит более одного склада с имнем  " + oldName + "!");
        }

        Warehouse warehouse = warehouses.get(0);

        if (warehouse.getName().equals(newName)) {
            throw new WarehouseValidationException("Необходимо ввести новое имя склада!");
        }

        warehouse.setName(newName);

        warehouseRepository.save(warehouse);

        return warehouse;

    }

    public Warehouse deleteWarehouse(String name) {
        List<Warehouse> warehouses = warehouseRepository.findWarehouseByName(name);
        if (warehouses.isEmpty()) {
            throw new WarehouseValidationException("Склад с именем  " + name + " не существует!");
        }

        if (warehouses.size() > 1) {
            throw new WarehouseValidationException("БД содержит более одного склада с именем  " + name + "!");
        }

        Warehouse warehouse = warehouses.get(0);

        String deletedName = deleteCountService.defineDeletedWarehouseName(warehouse);

        warehouse.setName(deletedName);

        warehouse.setDeleted(true);

        warehouseRepository.save(warehouse);

        return warehouse;
    }

}

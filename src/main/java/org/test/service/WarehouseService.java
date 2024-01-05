package org.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dto.warehouse.WarehouseDto;
import org.test.entity.Warehouse;
import org.test.exception.WarehouseValidationException;
import org.test.helpers.DeletionCountService;
import org.test.repository.WarehouseRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final DeletionCountService deletionCountService;

    @Autowired
    public WarehouseService(WarehouseRepository warehouseRepository, DeletionCountService deletionCountService) {
        this.warehouseRepository = warehouseRepository;
        this.deletionCountService = deletionCountService;
    }

    public List<WarehouseDto> getWarehouses() {
        return warehouseRepository.findAllByDeleted(false)
                .stream()
                .map(WarehouseDto::create)
                .collect(Collectors.toList());
    }

    public WarehouseDto getWarehouse(String name) {
        Warehouse warehouse = getWarehouseByName(name);
        return WarehouseDto.create(warehouse);
    }

    public WarehouseDto addNewWarehouse(String name) {
        Optional<Warehouse> existingWarehouse = warehouseRepository.findWarehouseByName(name);
        if (existingWarehouse.isPresent()) {
            throw new WarehouseValidationException("Склад с именем " + name + " уже существует!");
        }
        Warehouse warehouse = new Warehouse(name);
        warehouseRepository.save(warehouse);

        return WarehouseDto.create(warehouse);
    }

    @Transactional
    public WarehouseDto updateWarehouse(String oldName, String newName) {
        Warehouse warehouseForUpdating = getWarehouseByName(oldName);
        if (warehouseForUpdating.getName().equals(newName)) {
            throw new WarehouseValidationException("Необходимо ввести новое имя склада!");
        }

        warehouseForUpdating.setName(newName);
        warehouseRepository.save(warehouseForUpdating);

        return WarehouseDto.create(warehouseForUpdating);
    }

    public WarehouseDto deleteWarehouse(String name) {
        Warehouse warehouse = getWarehouseByName(name);
        String deletedName = deletionCountService.defineDeletedWarehouseName(warehouse);
        warehouse.setName(deletedName);
        warehouse.setDeleted(true);
        warehouseRepository.save(warehouse);

        return WarehouseDto.create(warehouse);
    }

    private Warehouse getWarehouseByName(String name) {
        return warehouseRepository.findWarehouseByName(name)
                .orElseThrow(() -> new WarehouseValidationException("Склад с именем  " + name + " не существует!"));
    }

}

package org.warehouse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dao.WarehouseRepository;
import org.warehouse.app.dto.warehouse.WarehouseDto;
import org.warehouse.app.exception.WarehouseValidationException;
import org.warehouse.app.model.WarehouseEntity;
import org.warehouse.app.util.DeletionCountService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final DeletionCountService deletionCountService;

    private final MessageSource messageSource;

    @Autowired
    public WarehouseService(WarehouseRepository warehouseRepository, DeletionCountService deletionCountService,
                            MessageSource messageSource) {
        this.warehouseRepository = warehouseRepository;
        this.deletionCountService = deletionCountService;
        this.messageSource = messageSource;
    }

    public List<WarehouseDto> getWarehouses() {
        return warehouseRepository.findAllByDeleted(false)
                .stream()
                .map(WarehouseDto::create)
                .collect(Collectors.toList());
    }

    public WarehouseDto getWarehouse(String name) {
        WarehouseEntity warehouseEntity = getWarehouseByName(name);
        return WarehouseDto.create(warehouseEntity);
    }

    public WarehouseDto addNewWarehouse(String name) {
        Optional<WarehouseEntity> existingWarehouse = warehouseRepository.findWarehouseByName(name);
        if (existingWarehouse.isPresent()) {
            throw new WarehouseValidationException(messageSource.getMessage("warehouse.service.already.exists",
                    new Object[]{name}, LocaleContextHolder.getLocale()));
        }
        WarehouseEntity warehouseEntity = new WarehouseEntity(name);
        warehouseRepository.save(warehouseEntity);

        return WarehouseDto.create(warehouseEntity);
    }

    @Transactional
    public WarehouseDto updateWarehouse(String oldName, String newName) {
        WarehouseEntity warehouseForUpdating = getWarehouseByName(oldName);
        if (warehouseForUpdating.getName().equals(newName)) {
            throw new WarehouseValidationException(messageSource.getMessage("warehouse.service.invalid.new.name",
                    null, LocaleContextHolder.getLocale()));
        }

        warehouseForUpdating.setName(newName);
        warehouseRepository.save(warehouseForUpdating);

        return WarehouseDto.create(warehouseForUpdating);
    }

    public WarehouseDto deleteWarehouse(String name) {
        WarehouseEntity warehouseEntity = getWarehouseByName(name);
        String deletedName = deletionCountService.defineDeletedWarehouseName(warehouseEntity);
        warehouseEntity.setName(deletedName);
        warehouseEntity.setDeleted(true);
        warehouseRepository.save(warehouseEntity);

        return WarehouseDto.create(warehouseEntity);
    }

    private WarehouseEntity getWarehouseByName(String name) {
        return warehouseRepository.findWarehouseByName(name)
                .orElseThrow(() -> new WarehouseValidationException(messageSource.getMessage("warehouse.service.not.exists",
                        new Object[]{name}, LocaleContextHolder.getLocale())));
    }

}

package org.test.helpers;

import org.springframework.stereotype.Component;
import org.test.exception.ProductValidationException;
import org.test.exception.WarehouseValidationException;

@Component
public class EntityRequestValidationService {

    public void validateProductAddRequest(String articul, String name) {

        if (articul == null || articul.isEmpty() || name == null || name.isEmpty()) {
            throw new ProductValidationException("Неправильный запрос на добавление продукта!");
        }
    }

    public void validateProductUpdateRequest(String articul, String name) {

        if (articul == null || articul.isEmpty() || name == null || name.isEmpty()) {
            throw new ProductValidationException("Неправильный запрос на обновление имени продукта!");
        }
    }

    public void validateWarehouseAddRequest(String name) {

        if (name == null || name.isEmpty()) {
            throw new WarehouseValidationException("Неправильный запрос на добавление склада!");
        }
    }

    public void validateWarehouseUpdateRequest(String oldName, String newName) {

        if (oldName == null || oldName.isEmpty() || newName == null || newName.isEmpty()) {
            throw new WarehouseValidationException("Неправильный запрос на обновление имени склада!");
        }
    }
}

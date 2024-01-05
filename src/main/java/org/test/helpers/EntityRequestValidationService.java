package org.test.helpers;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Service;
import org.test.exception.ProductValidationException;
import org.test.exception.WarehouseValidationException;

@Service
public class EntityRequestValidationService {

    public void validateProductAddRequest(String article, String name) {
        if (StringUtils.isEmpty(article)) {
            throw new ProductValidationException("Неправильный запрос на добавление продукта!");
        }

        if (StringUtils.isEmpty(name)) {
            throw new ProductValidationException("Неправильный запрос на добавление продукта!");
        }
    }

    public void validateProductUpdateRequest(String article, String name) {
        if (StringUtils.isEmpty(article)) {
            throw new ProductValidationException("Неправильный запрос на обновление имени продукта!");
        }

        if (StringUtils.isEmpty(name)) {
            throw new ProductValidationException("Неправильный запрос на обновление имени продукта!");
        }
    }

    public void validateWarehouseAddRequest(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new WarehouseValidationException("Неправильный запрос на добавление склада!");
        }
    }

    public void validateWarehouseUpdateRequest(String oldName, String newName) {
        if (StringUtils.isEmpty(oldName)) {
            throw new WarehouseValidationException("Неправильный запрос на обновление имени склада!");
        }

        if (StringUtils.isEmpty(newName)) {
            throw new WarehouseValidationException("Неправильный запрос на обновление имени склада!");
        }
    }
}

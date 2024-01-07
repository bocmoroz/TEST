package org.warehouse.app.helpers;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.exception.ProductValidationException;
import org.warehouse.app.exception.WarehouseValidationException;

@Service
public class EntityRequestValidationService {

    private final MessageSource messageSource;

    @Autowired
    public EntityRequestValidationService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void validateProductAddRequest(String article, String name) {
        if (StringUtils.isEmpty(article)) {
            throw new ProductValidationException(messageSource.getMessage("validation.add.product.invalid.article",
                    null, LocaleContextHolder.getLocale()));
        }

        if (StringUtils.isEmpty(name)) {
            throw new ProductValidationException(messageSource.getMessage("validation.add.product.invalid.name",
                    null, LocaleContextHolder.getLocale()));
        }
    }

    public void validateProductUpdateRequest(String article, String name) {
        if (StringUtils.isEmpty(article)) {
            throw new ProductValidationException(messageSource.getMessage("validation.update.product.invalid.article",
                    null, LocaleContextHolder.getLocale()));
        }

        if (StringUtils.isEmpty(name)) {
            throw new ProductValidationException(messageSource.getMessage("validation.update.product.invalid.new.name",
                    null, LocaleContextHolder.getLocale()));
        }
    }

    public void validateWarehouseAddRequest(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new WarehouseValidationException(messageSource.getMessage("validation.add.warehouse.invalid.name",
                    null, LocaleContextHolder.getLocale()));
        }
    }

    public void validateWarehouseUpdateRequest(String oldName, String newName) {
        if (StringUtils.isEmpty(oldName)) {
            throw new WarehouseValidationException(messageSource.getMessage("validation.update.warehouse.invalid.old.name",
                    null, LocaleContextHolder.getLocale()));
        }

        if (StringUtils.isEmpty(newName)) {
            throw new WarehouseValidationException(messageSource.getMessage("validation.update.warehouse.invalid.new.name",
                    null, LocaleContextHolder.getLocale()));
        }
    }
}

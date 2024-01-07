package org.warehouse.app.dto.income;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.warehouse.app.dto.ProductTransportationDto;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductIncomeDto extends ProductTransportationDto {

    private BigDecimal price;

    @Override
    public boolean checkFieldsForUploading() {
        return super.checkFieldsForUploading() || price == null || price.compareTo(BigDecimal.ZERO) < 0;
    }
}

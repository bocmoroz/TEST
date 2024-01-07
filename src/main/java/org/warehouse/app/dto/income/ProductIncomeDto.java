package org.warehouse.app.dto.income;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.warehouse.app.dto.ProductTransportationDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductIncomeDto extends ProductTransportationDto {

    private Long price;

    @Override
    public boolean checkFieldsForUploading() {
        return super.checkFieldsForUploading() || price == null || price < 0;
    }
}

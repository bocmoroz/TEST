package org.warehouse.app.dto.sale;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.warehouse.app.dto.ProductTransportationDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSaleDto extends ProductTransportationDto {

    private Long price;

    @Override
    public boolean checkFieldsForUploading() {
        return super.checkFieldsForUploading() || price == null || price < 0;
    }

}

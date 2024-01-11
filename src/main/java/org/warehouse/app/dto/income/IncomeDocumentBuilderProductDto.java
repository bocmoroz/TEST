package org.warehouse.app.dto.income;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.warehouse.app.dto.moving.MovingDocumentBuilderProductDto;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IncomeDocumentBuilderProductDto extends MovingDocumentBuilderProductDto {

    private BigDecimal price;

    @Override
    public boolean checkFieldsForUploading() {
        return super.checkFieldsForUploading() || price == null || price.compareTo(BigDecimal.ZERO) < 0;
    }
}

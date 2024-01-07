package org.warehouse.app.dto;

import lombok.Data;
import org.codehaus.plexus.util.StringUtils;

@Data
public class ProductTransportationDto {

    private String article;
    private String name;
    private Integer count;

    public boolean checkFieldsForUploading() {
        return StringUtils.isEmpty(article) || StringUtils.isEmpty(name) || count == null || count < 0;
    }

}

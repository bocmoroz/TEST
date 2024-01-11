package org.warehouse.app.dto.moving;

import lombok.Data;
import org.codehaus.plexus.util.StringUtils;

@Data
public class MovingDocumentBuilderProductDto {

    private String article;
    private String name;
    private Integer count;

    public boolean checkFieldsForUploading() {
        return StringUtils.isEmpty(article) || StringUtils.isEmpty(name) || count == null || count < 0;
    }

}

package org.warehouse.app.enums;

public enum TransportationDocumentTypeEnum {

    INCOME("Income", true),
    SALE("Sale", true),
    MOVING("Moving", false);

    private String name;
    private boolean containsPrice;

    TransportationDocumentTypeEnum(String name, boolean containsPrice) {
        this.name = name;
        this.containsPrice = containsPrice;
    }

    public boolean isContainsPrice() {
        return containsPrice;
    }

    public String getName() {
        return name;
    }
}

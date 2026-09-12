package com.ecom.gofitEcommerce.enums;

public enum UserRole {
    ADMIN("Admin"),
    CUSTOMER("Customer");

    private final String dbValue;

    UserRole(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}


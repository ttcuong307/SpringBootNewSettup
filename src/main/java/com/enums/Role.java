package com.enums;

public enum Role {
    ROLE_USER(1),
    ROLE_ADMIN(0);

    private int value;

    private Role(int value) {
        this.value = value;
    }

    public String getAuthority() {
        return name();
    }
}

package org.designerchat.common;

public enum ChatRole {
    ASSISTANT, USER;

    public static ChatRole fromString(String value) {
        return valueOf(value.toUpperCase());
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}

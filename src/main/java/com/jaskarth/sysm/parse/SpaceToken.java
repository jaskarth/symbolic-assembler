package com.jaskarth.sysm.parse;

public record SpaceToken() implements Token {
    @Override
    public String value() {
        return " ";
    }
}

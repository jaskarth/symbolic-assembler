package com.jaskarth.sysm.parse;

public record SymbolToken(char symbol) implements Token {
    @Override
    public String value() {
        return String.valueOf(symbol);
    }
}

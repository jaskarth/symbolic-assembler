package com.jaskarth.sysm.parse;

public sealed interface Token permits SpaceToken, StringToken, SymbolToken {
    String value();
}

package com.jaskarth.sysm.parse;

import java.util.ArrayList;
import java.util.List;

public class Tokenize {
    public static TokenState tokenize(String classFile) {
        classFile = classFile.replaceAll("\n", "");

        List<Token> tokens = new ArrayList<>();
        int len = classFile.length();
        StringBuilder sb = new StringBuilder();
        boolean lastSpace = false;

        for (int i = 0; i < len; i++) {
            char c = classFile.charAt(i);
            if (Character.isAlphabetic(c) || Character.isDigit(c)) {
                sb.append(c);
                lastSpace = false;
            } else if (
                    c == '(' || c == ')' || c == '{' || c == '}' ||
                    c == '-' || c == ',' || c == '>' || c == '<' ||
                    c == ';' || c == ':' || c == '.' || c == '\"'
            ) {
                if (!sb.isEmpty()) {
                    tokens.add(new StringToken(sb.toString()));
                    sb.setLength(0);
                }

                tokens.add(new SymbolToken(c));
                lastSpace = false;
            } else if (c == ' ' || c == '\t') {
                if (!sb.isEmpty()) {
                    tokens.add(new StringToken(sb.toString()));
                    sb.setLength(0);
                }

                if (!lastSpace) {
                    tokens.add(new SpaceToken());
                }

                lastSpace = true;
            } else {
                throw new RuntimeException("Invalid character in file: '" + c + "'");
            }
        }

        return new TokenState(tokens);
    }
}

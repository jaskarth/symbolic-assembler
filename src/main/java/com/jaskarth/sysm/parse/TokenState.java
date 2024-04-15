package com.jaskarth.sysm.parse;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class TokenState {
    private final Deque<Token> tokens;

    public TokenState(List<Token> tokens) {
        this.tokens = new ArrayDeque<>(tokens);
    }

    private void ensure() {
        if (tokens.isEmpty()) {
            throw new RuntimeException("Reached end of file when parsing!");
        }
    }

    public Token next() {
        ensure();
        return tokens.removeFirst();
    }

    public void consumeSpace() {
        ensure();

        Token here = tokens.poll();
        if (here instanceof SpaceToken) {
            next();
        }
    }
}

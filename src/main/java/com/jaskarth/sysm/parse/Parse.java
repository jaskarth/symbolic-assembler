package com.jaskarth.sysm.parse;

import org.objectweb.asm.tree.ClassNode;

public class Parse {
    public static ClassNode parse(TokenState tokens) {
        ClassNode cl = new ClassNode();

        tokens.consumeSpace();
        Token next = tokens.next();
        String pkg = "";



        return cl;
    }

    private static String getPackage(TokenState tokens) {
        Token tk = tokens.next();
        if (tk instanceof StringToken str) {
            if ("package".equals(str.value())) {

            } else {
                return "";
            }
        } else {
            throw new RuntimeException("Found unexpected token " + tk.value());
        }
				
				throw new RuntimeException("Couldn't parse this, sorry!");
    }
}

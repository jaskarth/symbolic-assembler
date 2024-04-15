package com.jaskarth.sysm.write;

import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ImportCollector {
    private final Set<String> imports = new LinkedHashSet<>();
    private final String name;

    public ImportCollector(String name) {
        this.name = name.replace('/', '.');
    }

    public String getName(Type type) {
        if (type.getSort() == Type.OBJECT) {
            String name = type.getClassName();

            if (name.contains(".")) {
                imports.add(name);

                return name.substring(name.lastIndexOf('.') + 1);
            }
        } else if (type.getSort() == Type.ARRAY) {
            String name = type.getClassName();

            if (name.contains(".")) {
                imports.add(type.getElementType().getClassName());
                return name.substring(name.lastIndexOf('.') + 1);
            }
        }

        return type.getClassName();
    }

    public String getFromInternalName(String name) {
        name = name.replace('/', '.');

        if (name.contains(".")) {
            imports.add(name);

            return name.substring(name.lastIndexOf('.') + 1);
        }

        return name;
    }

    public Set<String> getImports() {
        imports.remove(this.name);
        return this.imports;
    }
}

package com.jaskarth.sysm.write;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Writer {
    private static final Map<Integer, String> CLASS_MODS = new LinkedHashMap<>();
    private static final Map<Integer, String> FIELD_MODS = new LinkedHashMap<>();
    private static final Map<Integer, String> CLASS_TYPES = new HashMap<>();
    static {
        CLASS_MODS.put(Opcodes.ACC_PUBLIC, "public");
        CLASS_MODS.put(Opcodes.ACC_PROTECTED, "protected");
        CLASS_MODS.put(Opcodes.ACC_PRIVATE, "private");
        CLASS_MODS.put(Opcodes.ACC_FINAL, "final");
        CLASS_MODS.put(Opcodes.ACC_STATIC, "static");
        CLASS_MODS.put(Opcodes.ACC_ABSTRACT, "abstract");
        CLASS_MODS.put(Opcodes.ACC_SYNTHETIC, "synthetic");

        FIELD_MODS.put(Opcodes.ACC_PUBLIC, "public");
        FIELD_MODS.put(Opcodes.ACC_PROTECTED, "protected");
        FIELD_MODS.put(Opcodes.ACC_PRIVATE, "private");
        FIELD_MODS.put(Opcodes.ACC_FINAL, "final");
        FIELD_MODS.put(Opcodes.ACC_STATIC, "static");
        FIELD_MODS.put(Opcodes.ACC_VOLATILE, "volatile");
        FIELD_MODS.put(Opcodes.ACC_SYNTHETIC, "synthetic");
        FIELD_MODS.put(Opcodes.ACC_TRANSIENT, "transient");

        CLASS_TYPES.put(Opcodes.ACC_INTERFACE, "interface");
        CLASS_TYPES.put(Opcodes.ACC_ENUM, "enum");
        CLASS_TYPES.put(Opcodes.ACC_RECORD, "record");
    }

    public static String write(ClassNode cl) {
        ImportCollector ic = new ImportCollector(cl.name);
        TextBuilder tb = new TextBuilder();
        int acc = cl.access;

        tb.append(getClassAccess(acc));
        tb.append(getType(acc));
        tb.sp();
        tb.append(ic.getFromInternalName(cl.name));
        tb.append(" ");
        supers(cl, tb, ic);

        tb.append("{");
        tb.ln();

        version(tb, cl.version);
        sourcefile(cl, tb);
        fields(cl, tb);

        for (MethodNode mt : cl.methods) {
            tb.ln();
            tb.append(MethodWriter.write(mt, ic));
        }

        // TODO:
        //  permittedSubclasses
        //  innerClasses
        //  nestHostClass
        //  nestMembers
        //  annotations

        tb.append("}");

        TextBuilder out = new TextBuilder();

        String pkgName = cl.name.replace('/', '.');

        if (pkgName.contains(".")) {
            pkgName = pkgName.substring(0, pkgName.lastIndexOf("."));
            out.append("package ");
            out.append(pkgName);
            out.scln();
            out.ln();
        }

        for (String imp : ic.getImports()) {
            out.append("import ");
            out.append(imp);
            out.scln();
        }

        if (!ic.getImports().isEmpty()) {
            out.ln();
        }

        out.append(tb.toString());

        return out.toString();
    }

    private static void fields(ClassNode cl, TextBuilder tb) {
        if (!cl.fields.isEmpty()) {
            tb.ln();

            tb.indent(1).append("fields {").ln();
            for (FieldNode f : cl.fields) {
                tb.indent(2);
                tb.append(getFieldAccess(f.access));
                tb.append(f.desc);
                tb.sp();
                tb.append(f.name);
                // TODO: default value
                tb.scln();
            }
            tb.indent(1).append("}");
            tb.ln();
        }
    }

    private static void supers(ClassNode cl, TextBuilder tb, ImportCollector ic) {
        String extend = cl.superName;
        if (extend != null && !extend.equals("java/lang/Object")) {
            tb.append("extends ").append(ic.getFromInternalName(extend)).append(" ");
        }

        if (!cl.interfaces.isEmpty()) {
            tb.append("implements ");
            int size = cl.interfaces.size();

            for (int i = 0; i < size; i++) {
                tb.append(ic.getFromInternalName(cl.interfaces.get(i)));
                if (i != size - 1) {
                    tb.append(",");
                }
                tb.sp();
            }
        }
    }

    private static void version(TextBuilder tb, int version) {
        tb.indent(1);
        tb.append("version ");
        tb.append(version);
        tb.scln();
    }

    private static void sourcefile(ClassNode cl, TextBuilder tb) {
        if (cl.sourceFile != null) {
            tb.indent(1);
            tb.append("sourcefile ");
            tb.appendStr(cl.sourceFile);
            tb.scln();
        }

        if (cl.sourceDebug != null) {
            tb.indent(1);
            tb.append("sourcedebug ");
            tb.appendStr(cl.sourceDebug);
            tb.scln();
        }
    }

    private static String getClassAccess(int acc) {
        StringBuilder sb = new StringBuilder();
        for (Integer t : CLASS_MODS.keySet()) {
            if ((acc & t) == t) {
                sb.append(CLASS_MODS.get(t)).append(" ");
            }
        }

        return sb.toString();
    }

    private static String getFieldAccess(int acc) {
        StringBuilder sb = new StringBuilder();
        for (Integer t : FIELD_MODS.keySet()) {
            if ((acc & t) == t) {
                sb.append(FIELD_MODS.get(t)).append(" ");
            }
        }

        return sb.toString();
    }

    public static String getType(int acc) {
        for (Integer t : CLASS_TYPES.keySet()) {
            if ((acc & t) == t) {
                return CLASS_MODS.get(t);
            }
        }

        return "class";
    }
}

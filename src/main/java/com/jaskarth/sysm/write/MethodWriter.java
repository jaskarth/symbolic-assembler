package com.jaskarth.sysm.write;

import com.jaskarth.sysm.util.Consts;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.*;

public final class MethodWriter {
    private static final Map<Integer, String> METHOD_MODS = new LinkedHashMap<>();
    static {
        METHOD_MODS.put(Opcodes.ACC_PUBLIC, "public");
        METHOD_MODS.put(Opcodes.ACC_PROTECTED, "protected");
        METHOD_MODS.put(Opcodes.ACC_PRIVATE, "private");
        METHOD_MODS.put(Opcodes.ACC_FINAL, "final");
        METHOD_MODS.put(Opcodes.ACC_STATIC, "static");
        METHOD_MODS.put(Opcodes.ACC_SYNCHRONIZED, "synchronized");
        METHOD_MODS.put(Opcodes.ACC_BRIDGE, "bridge");
        METHOD_MODS.put(Opcodes.ACC_VARARGS, "varargs");
        METHOD_MODS.put(Opcodes.ACC_NATIVE, "native");
        METHOD_MODS.put(Opcodes.ACC_ABSTRACT, "abstract");
        METHOD_MODS.put(Opcodes.ACC_STRICT, "strictfp");
        METHOD_MODS.put(Opcodes.ACC_SYNTHETIC, "synthetic");
    }
    
    public static TextBuilder write(MethodNode mt, ImportCollector ic) {
        TextBuilder tb = new TextBuilder();
        tb.indent(1);
        tb.append(getMethodAccess(mt.access));
        Type rt = Type.getType(mt.desc);
        tb.append(ic.getName(rt.getReturnType()));
        tb.sp();
        tb.append(mt.name);

        // params
        tb.append("(");

        Type[] types = Type.getArgumentTypes(mt.desc);
        for (int i = 0; i < types.length; i++) {
            tb.append(ic.getName(types[i]));
            if (i != types.length - 1) {
                tb.append(", ");
            }
        }

        tb.append(") ");

        if (!mt.exceptions.isEmpty()) {
            tb.append("throws ");
            List<String> exceptions = mt.exceptions;
            for (int i = 0; i < exceptions.size(); i++) {
                String ex = exceptions.get(i);
                tb.append(ex);

                if (i != exceptions.size() - 1) {
                    tb.append(",");
                }
                tb.sp();
            }
        }

        tb.append("{").ln();

        Map<Label, Integer> lbIds = new HashMap<>();

        code(mt, tb, lbIds, ic);
        handlers(mt, tb, lbIds);
        vars(mt, tb, lbIds, ic);
        lines(mt, tb, lbIds);

        tb.indent(1).append("}").ln();

        return tb;
    }

    private static void code(MethodNode mt, TextBuilder tb, Map<Label, Integer> lbIds, ImportCollector ic) {
        if ((mt.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
            return;
        }

        tb.indent(2);
        tb.append("code {");
        tb.ln();

        for (AbstractInsnNode insn : mt.instructions) {
            if (insn instanceof LabelNode lb) {
                Label lbl = lb.getLabel();
                lbIds.computeIfAbsent(lbl, l -> lbIds.size());
            }
        }

        boolean lastLabel = false;
        for (AbstractInsnNode insn : mt.instructions) {
            if (insn instanceof LabelNode lb) {
                Label lbl = lb.getLabel();
                tb.indent(2);
                tb.append("L");
                tb.append(lbIds.get(lbl));
                tb.append(":");
                tb.ln();
            } else if (insn.getOpcode() != -1) {
                int opc = insn.getOpcode();
                tb.indent(3);
                tb.append(Consts.OPCODES[opc]);

                if (insn instanceof IntInsnNode iinsn) {
                    tb.sp();
                    tb.append(iinsn.operand);
                } else if (insn instanceof VarInsnNode vinsn) {
                    tb.sp();
                    tb.append(vinsn.var);
                } else if (insn instanceof TypeInsnNode tinsn) {
                    tb.sp();
                    tb.append(ic.getFromInternalName(tinsn.desc));
                } else if (insn instanceof LdcInsnNode linsn) {
                    tb.sp();
                    // TODO: handle methodhandle and condy
                    if (linsn.cst instanceof String st) {
                        tb.appendStr(st);
                    } else {
                        tb.append(linsn.cst);
                    }
                } else if (insn instanceof JumpInsnNode jinsn) {
                    tb.sp();
                    Label lbl = jinsn.label.getLabel();
                    tb.append("L");
                    tb.append(lbIds.get(lbl));
                } else if (insn instanceof IincInsnNode iinsn) {
                    tb.sp();
                    tb.append(iinsn.var);
                    tb.sp();
                    tb.append(iinsn.incr);
                } else if (insn instanceof FieldInsnNode finsn) {
                    tb.sp();
                    tb.append(ic.getName(Type.getType(finsn.desc)));
                    tb.sp();
                    tb.append(ic.getFromInternalName(finsn.owner));
                    tb.append(".");
                    tb.append(finsn.name);

                } else if (insn instanceof MethodInsnNode minsn) {
                    tb.sp();
                    tb.append(ic.getName(Type.getReturnType(minsn.desc)));
                    tb.sp();
                    tb.append(ic.getFromInternalName(minsn.owner));
                    tb.append(".");
                    tb.append(minsn.name);
                    tb.append("(");
                    Type[] types = Type.getArgumentTypes(minsn.desc);
                    for (int i = 0; i < types.length; i++) {
                        tb.append(ic.getName(types[i]));

                        if (i != types.length - 1) {
                            tb.append(", ");
                        }
                    }
                    tb.append(")");
                }

                tb.scln();
            }
        }

        tb.indent(2);
        tb.append("}");
        tb.ln();
    }

    private static void handlers(MethodNode mt, TextBuilder tb, Map<Label, Integer> lbIds) {
        if (mt.tryCatchBlocks.isEmpty()) {
            return;
        }

        tb.ln();
        tb.indent(2);
        tb.append("handlers {");
        tb.ln();

        for (TryCatchBlockNode handler : mt.tryCatchBlocks) {
            tb.indent(3);
            tb.append("try (L");
            tb.append(lbIds.get(handler.start.getLabel()));
            tb.append(" to L");
            tb.append(lbIds.get(handler.end.getLabel()));
            if (handler.type == null) {
                tb.append(") finally ");
            } else {
                tb.append(") catch (");
                tb.append(handler.type);
                tb.append(") ");
            }

            tb.append("L");
            tb.append(lbIds.get(handler.handler.getLabel()));
            tb.scln();
        }

        tb.indent(2);
        tb.append("}");
        tb.ln();
    }

    private static void vars(MethodNode mt, TextBuilder tb, Map<Label, Integer> lbIds, ImportCollector ic) {
        tb.ln();
        tb.indent(2);
        tb.append("vars {");
        tb.ln();

        tb.indent(3);
        tb.append("stack ");
        tb.append(mt.maxStack);
        tb.scln();

        tb.indent(3);
        tb.append("locals ");
        tb.append(mt.maxLocals);
        tb.scln();

        if (mt.localVariables != null && !mt.localVariables.isEmpty()) {
            tb.ln();

            List<LocalVariableNode> vars = new ArrayList<>(mt.localVariables);
            vars.sort(Comparator.comparing(c -> c.index));

            for (LocalVariableNode lv : vars) {
                tb.indent(3);
                tb.append("local ");
                tb.append(lv.index);
                tb.append(" (L");
                tb.append(lbIds.get(lv.start.getLabel()));
                tb.append(" to L");
                tb.append(lbIds.get(lv.end.getLabel()));
                tb.append(") ");
                tb.append(ic.getName(Type.getType(lv.desc)));
                tb.sp();
                tb.append(lv.name);
                tb.scln();
            }
        }

        tb.indent(2);
        tb.append("}");
        tb.ln();
    }

    private static void lines(MethodNode mt, TextBuilder tb, Map<Label, Integer> lbIds) {
        Map<Label, Integer> lineNumbers = new LinkedHashMap<>();

        for (AbstractInsnNode insn : mt.instructions) {
            if (insn instanceof LineNumberNode ln) {
                lineNumbers.put(ln.start.getLabel(), ln.line);
            }
        }

        if (lineNumbers.isEmpty()) {
            return;
        }

        tb.ln();
        tb.indent(2);
        tb.append("lines {");
        tb.ln();

        for (Map.Entry<Label, Integer> e : lineNumbers.entrySet()) {
            tb.indent(3);
            tb.append("line L");
            tb.append(lbIds.get(e.getKey()));
            tb.append(" -> ");
            tb.append(e.getValue());
            tb.scln();
        }

        tb.indent(2);
        tb.append("}");
        tb.ln();
    }

    private static String getMethodAccess(int acc) {
        StringBuilder sb = new StringBuilder();
        for (Integer t : METHOD_MODS.keySet()) {
            if ((acc & t) == t) {
                sb.append(METHOD_MODS.get(t)).append(" ");
            }
        }

        return sb.toString();
    }
}
